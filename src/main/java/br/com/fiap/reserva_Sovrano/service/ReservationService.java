package br.com.fiap.reserva_Sovrano.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservations> listAll() {
        return reservationRepository.findAll();
    }

    public List<Reservations> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservations> listByTable(Long tableId) {
        return reservationRepository.findByTableId(tableId);
    }

    public Reservations create(Reservations reservation) {
        if(reservation.getReservationDateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Reservation date must be in the future.");
        }

        boolean isOccupied = reservationRepository.existsByTableIdAndReservationDateTimeAndStatus(
            reservation.getTableId(),
            reservation.getReservationDateTime(),
            StatusReservation.CONFIRMED
        );

        if(isOccupied) {
            throw new IllegalStateException("Table is already reserved for this date and time.");
        }

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }

    public Reservations confirm(Long id) {
        Reservations reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        reservation.setStatus(StatusReservation.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public void cancel(Long id) {
        Reservations reservation = reservationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found."));
        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        if(!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        reservationRepository.deleteById(id);
    }

    
}

