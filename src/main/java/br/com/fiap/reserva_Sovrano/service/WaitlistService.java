package br.com.fiap.reserva_Sovrano.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.PriorityType;
import br.com.fiap.reserva_Sovrano.components.WaitlistStatus;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.Waitlist;
import br.com.fiap.reserva_Sovrano.model.dto.MyWaitlistDTO;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.repository.WaitlistRepository;
import br.com.fiap.reserva_Sovrano.utils.GlobalUtils;
import br.com.fiap.reserva_Sovrano.utils.WaitlistUtils;

@Service
public class WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private WaitlistUtils waitlistUtils;

    @Autowired
    private GlobalUtils globalUtils;

    // -----------------------------------------------------------
    // ADICIONAR À FILA
    // -----------------------------------------------------------
    public Waitlist joinWaitlist(Long userId, int peopleCount, Period period,boolean hasLegalPriority, String legalReason) {

        LocalDate today = LocalDate.now();

        // impedir duplicação
        waitlistRepository.findByUserIdAndDateAndPeriod(userId, today, period)
                .ifPresent(w -> {
                    throw new IllegalStateException("Você já está na fila de espera para este período.");
                });

        globalUtils.validateLegalPriority(hasLegalPriority, legalReason);

        Waitlist entry = Waitlist.builder()
                .userId(userId)
                .peopleCount(peopleCount)
                .date(today)
                .period(period)
                .createdAt(LocalDateTime.now())
                .status(WaitlistStatus.WAITING)
                .hasLegalPriority(hasLegalPriority)
                .legalPriorityReason(hasLegalPriority ? legalReason : null)
                .build();

        Waitlist saved = waitlistRepository.save(entry);

        // enviar e-mail com a posição atual (entrar na fila)
        List<Waitlist> sameList = orderWaitlist(
            waitlistRepository.findByDateAndPeriodAndStatusOrderByCreatedAt(
                saved.getDate(), saved.getPeriod(), WaitlistStatus.WAITING
            )
        );

        int pos = -1;
        for (int i = 0; i < sameList.size(); i++) {
            if (java.util.Objects.equals(sameList.get(i).getId(), saved.getId())) { pos = i + 1; break; }
        }

        var user = userRepository.findById(saved.getUserId()).orElse(null);
        if (user != null) notificationService.sendWaitlistPositionEmail(user, saved, pos);

        return saved;
    }

    // -----------------------------------------------------------
    // RETORNAR FILA COMPLETA (ADMIN)
    // -----------------------------------------------------------
    public List<Waitlist> getWaitlist(LocalDate date, Period period) {
        return orderWaitlist(
            waitlistRepository.findByDateAndPeriodAndStatusOrderByCreatedAt(
                    date, period, WaitlistStatus.WAITING
            )
        );
    }

    public List<MyWaitlistDTO> getMyWaitlists(Long userId) {

        List<Waitlist> entries = waitlistRepository
                .findByUserIdAndStatusOrderByCreatedAt(userId, WaitlistStatus.WAITING);

        List<MyWaitlistDTO> result = new ArrayList<>();

        for (Waitlist w : entries) {

            // pega a lista completa do dia/período
            List<Waitlist> sameList = orderWaitlist(
                    waitlistRepository.findByDateAndPeriodAndStatusOrderByCreatedAt(
                            w.getDate(),
                            w.getPeriod(),
                            WaitlistStatus.WAITING
                    )
            );

            // calcula posição
            int pos = -1;
            for (int i = 0; i < sameList.size(); i++) {
                if (java.util.Objects.equals(sameList.get(i).getId(), w.getId())) {
                    pos = i + 1;
                    break;
                }
            }

            // monta DTO
            result.add(
                    MyWaitlistDTO.builder()
                            .id(w.getId())
                            .date(w.getDate())
                            .period(w.getPeriod())
                            .peopleCount(w.getPeopleCount())
                            .status(w.getStatus())
                            .position(pos)
                            .build()
            );
        }

        return result;
    }


    // -----------------------------------------------------------
    // POSIÇÃO NA FILA
    // -----------------------------------------------------------
   public int getUserPosition(Long userId, Period period, LocalDate date) {

        List<Waitlist> list = orderWaitlist(
                waitlistRepository.findByDateAndPeriodAndStatusOrderByCreatedAt(
                        date, period, WaitlistStatus.WAITING
                )
        );

        for (int i = 0; i < list.size(); i++) {
            if (java.util.Objects.equals(list.get(i).getUserId(), userId)) {
                return i + 1;
            }
        }

        return -1;
    }

    // -----------------------------------------------------------
    // PROCESSAR FILA QUANDO UMA MESA FOR LIBERADA
    // -----------------------------------------------------------
    public void processNextInLine(Long tableId) {

        LocalDate today = LocalDate.now();
        Period period = waitlistUtils.getCurrentPeriod();

        List<Waitlist> ordered = orderWaitlist(
                waitlistRepository.findByDateAndPeriodAndStatusOrderByCreatedAt(
                        today, period, WaitlistStatus.WAITING
                )
        );

        if (ordered.isEmpty()) return;

        Waitlist next = ordered.get(0);

        // marcar como notificado
        next.setStatus(WaitlistStatus.NOTIFIED);
        waitlistRepository.save(next);

        // criar uma reserva temporária aguardando confirmação
        var reservation = waitlistUtils.createPendingWaitlistReservation(next, tableId);

        // enviar e-mail de notificação com link de confirmação
        var user = userRepository.findById(next.getUserId()).orElse(null);
        if (user != null) notificationService.sendWaitlistNotifiedEmail(user, next, reservation);
    }

    // -----------------------------------------------------------
    // ADMIN: deletar uma entrada da waitlist
    public void deleteEntry(Long id) {
        GlobalUtils.check(!waitlistRepository.existsById(id), "Entrada da waitlist não encontrada.");
        waitlistRepository.deleteById(id);
    }

    // -----------------------------------------------------------
    // ADMIN: notificar uma entrada específica (manual)
    // cria a reserva PENDING usando o tableId informado
    public Waitlist notifyEntry(Long id, Long tableId) {
        Waitlist w = waitlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada da waitlist não encontrada."));

        if (w.getStatus() != WaitlistStatus.WAITING) {
            throw new IllegalStateException("Só é possível notificar entradas com status WAITING.");
        }

        w.setStatus(WaitlistStatus.NOTIFIED);
        waitlistRepository.save(w);

        // criar reserva temporária
        var reservation = waitlistUtils.createPendingWaitlistReservation(w, tableId);

        var user = userRepository.findById(w.getUserId()).orElse(null);
        if (user != null) notificationService.sendWaitlistNotifiedEmail(user, w, reservation);

        return w;
    }

    // -----------------------------------------------------------
    // ADMIN: expirar uma entrada (manual)
    public Waitlist expireEntry(Long id) {
        Waitlist w = waitlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada da waitlist não encontrada."));

        w.setStatus(WaitlistStatus.EXPIRED);
        return waitlistRepository.save(w);
    }

    // -----------------------------------------------------------
    // CUSTOMER: permite ao usuário remover sua própria entrada
    public void leaveWaitlist(Long id, Long userId) {
        Waitlist w = waitlistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrada da waitlist não encontrada."));

        if (!java.util.Objects.equals(w.getUserId(), userId)) {
            throw new IllegalStateException("Você não pode remover uma entrada de outro usuário.");
        }

        waitlistRepository.deleteById(id);
    }

    // -----------------------------------------------------------
    // ORDENAR POR PRIORIDADE
    private List<Waitlist> orderWaitlist(List<Waitlist> list) {
        return list.stream()
            .sorted((a, b) -> {

                Users ua = userRepository.findById(a.getUserId()).orElse(null);
                Users ub = userRepository.findById(b.getUserId()).orElse(null);

                // 1️⃣ PRIORIDADE LEGAL → maior prioridade absoluta
                int la = a.isHasLegalPriority() ? 0 : 1;
                int lb = b.isHasLegalPriority() ? 0 : 1;

                if (la != lb) return Integer.compare(la, lb);

                // 2️⃣ VIP (se não tiver prioridade legal)
                int va = PriorityType.NONE.getOrder();
                int vb = PriorityType.NONE.getOrder();

                if (ua != null && ua.getVipLevel() != null) va = ua.getVipLevel().getOrder();
                if (ub != null && ub.getVipLevel() != null) vb = ub.getVipLevel().getOrder();

                if (va != vb) return Integer.compare(va, vb);

                // 3️⃣ createdAt
                return a.getCreatedAt().compareTo(b.getCreatedAt());
            })
            .toList();
    }

    // -----------------------------------------------------------
    // LIMPAR FILA DE UM DIA (Scheduler)
    // -----------------------------------------------------------
    public void cleanWaitlistOfDay(LocalDate date) {
        List<Waitlist> all = waitlistRepository.findAll();

        List<Waitlist> remove = all.stream()
                .filter(w -> w.getDate().isBefore(date))
                .toList();

        waitlistRepository.deleteAll(remove);
    }
}

