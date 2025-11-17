package br.com.fiap.reserva_Sovrano.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.WaitlistStatus;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.Waitlist;
import br.com.fiap.reserva_Sovrano.model.dto.MyWaitlistDTO;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.repository.WaitlistRepository;
import br.com.fiap.reserva_Sovrano.utils.WaitlistUtils;

@Service
public class WaitlistService {

    @Autowired
    private WaitlistRepository waitlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WaitlistUtils waitlistUtils;

    // -----------------------------------------------------------
    // ADICIONAR À FILA
    // -----------------------------------------------------------
    public Waitlist joinWaitlist(Long userId, int peopleCount, Period period) {

        LocalDate today = LocalDate.now();

        // impedir duplicação
        waitlistRepository.findByUserIdAndDateAndPeriod(userId, today, period)
                .ifPresent(w -> {
                    throw new RuntimeException("Você já está na fila de espera para este período.");
                });

        Waitlist entry = Waitlist.builder()
                .userId(userId)
                .peopleCount(peopleCount)
                .date(today)
                .period(period)
                .createdAt(LocalDateTime.now())
                .status(WaitlistStatus.WAITING)
                .build();

        return waitlistRepository.save(entry);
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
                if (sameList.get(i).getId().equals(w.getId())) {
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
            if (list.get(i).getUserId().equals(userId)) {
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
        waitlistUtils.createPendingWaitlistReservation(next, tableId);
    }

    // -----------------------------------------------------------
    // ORDENAR POR PRIORIDADE
    // ordenação por prioridade (substitui a versão anterior)
    private List<Waitlist> orderWaitlist(List<Waitlist> list) {
        return list.stream()
            .sorted((a, b) -> {
                Users ua = userRepository.findById(a.getUserId()).orElse(null);
                Users ub = userRepository.findById(b.getUserId()).orElse(null);

                int pa = ua != null && ua.getPriorityType() != null ? ua.getPriorityType().getOrder() : Integer.MAX_VALUE;
                int pb = ub != null && ub.getPriorityType() != null ? ub.getPriorityType().getOrder() : Integer.MAX_VALUE;

                if (pa != pb) return Integer.compare(pa, pb);

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

