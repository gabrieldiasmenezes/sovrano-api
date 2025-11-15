package br.com.fiap.reserva_Sovrano.jobs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationUtils reservationMethods; // usa seu método de liberar mesa


    @Scheduled(cron = "0 */10 * * * *") // roda a cada 10 minutos
    public void processReservationStatuses() {

        LocalDateTime now = LocalDateTime.now();

        // 1️⃣ Cancelar pendentes com menos de 1h
        List<Reservations> pendentes = reservationRepository.findAllByStatus(StatusReservation.PENDING);

        pendentes.forEach(res -> {
            if (res.getReservationDateTime().isBefore(now.plusHours(1))) {
                res.setStatus(StatusReservation.CANCELLED);
                reservationMethods.setTableAvailability(res.getTableId(), true);
            }
        });

        // 2️⃣ Cancelar confirmadas atrasadas (+30 min)
        List<Reservations> confirmadas = reservationRepository.findAllByStatus(StatusReservation.CONFIRMED);

        confirmadas.forEach(res -> {
            if (res.getReservationDateTime().plusMinutes(30).isBefore(now)) {
                res.setStatus(StatusReservation.CANCELLED);
                reservationMethods.setTableAvailability(res.getTableId(), true);
            }
        });

        reservationRepository.saveAll(pendentes);
        reservationRepository.saveAll(confirmadas);
    }

    @Scheduled(cron = "0 0 3 * * *") // 03:00 da manhã
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
}