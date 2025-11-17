package br.com.fiap.reserva_Sovrano.jobs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.service.WaitlistService;

@Service
public class ReservationScheduler {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WaitlistService waitlistService;

    @Scheduled(cron = "0 */10 * * * *") // a cada 10 minutos
    public void processReservationStatuses() {

        LocalDateTime now = LocalDateTime.now();

        // 1️⃣ Cancelar pendentes com menos de 1h
        List<Reservations> pendentes = reservationRepository.findAllByStatus(StatusReservation.PENDING);

        pendentes.forEach(res -> {
            if (res.getReservationDateTime().isBefore(now.plusHours(1))) {
                res.setStatus(StatusReservation.CANCELLED);
                 waitlistService.processNextInLine(res.getTableId());
            }
            
        });

        // 2️⃣ Cancelar confirmadas atrasadas (+30 min)
        List<Reservations> confirmadas = reservationRepository.findAllByStatus(StatusReservation.CONFIRMED);

        confirmadas.forEach(res -> {

            if (res.getReservationDateTime().plusMinutes(30).isBefore(now)) {

                res.setStatus(StatusReservation.CANCELLED);

                waitlistService.processNextInLine(res.getTableId());

                Users user = userRepository.findById(res.getUserId()).orElse(null);

                if (user != null) {

                    user.setNoShowCount(user.getNoShowCount() + 1);

                    // Bloqueio automático
                    if (user.getNoShowCount() >= 3) {

                        user.setRole(UserRole.BLOCK);
                        user.setBlockedUntil(LocalDate.now().plusDays(30));

                        // Cancelar reservas ativas
                        List<Reservations> ativas = reservationRepository
                                .findByUserIdAndStatusIn(
                                        user.getId(),
                                        List.of(StatusReservation.PENDING, StatusReservation.CONFIRMED)
                                );

                        ativas.forEach(r -> r.setStatus(StatusReservation.CANCELLED));

                        reservationRepository.saveAll(ativas);
                    }

                    userRepository.save(user);
                }
            }
        });

        reservationRepository.saveAll(pendentes);
        reservationRepository.saveAll(confirmadas);
    }

    @Scheduled(cron = "0 0 3 * * *") // 03:00
    public void cleanOldReservations() {
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Reservations> deletar = reservationRepository
                .findByReservationDateTimeBetween(start, end);

        List<Reservations> filtrada = deletar.stream()
                .filter(r -> r.getStatus() == StatusReservation.CANCELLED
                        || r.getStatus() == StatusReservation.COMPLETED)
                .toList();

        reservationRepository.deleteAll(filtrada);
    }

    @Scheduled(cron = "0 0 4 * * *") // 04:00
    public void autoUnblockUsers() {

        List<Users> bloqueados = userRepository.findByRole(UserRole.BLOCK);
        LocalDate hoje = LocalDate.now();

        bloqueados.forEach(u -> {
            if (u.getBlockedUntil() != null && u.getBlockedUntil().isBefore(hoje)) {
                u.setRole(UserRole.CUSTOMER);
                u.setBlockedUntil(null);
                u.setNoShowCount(0);
            }
        });

        userRepository.saveAll(bloqueados);
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void cleanWaitlist() {
        waitlistService.cleanWaitlistOfDay(LocalDate.now());
    }
}
