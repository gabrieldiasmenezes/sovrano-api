package br.com.fiap.reserva_Sovrano.components;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;

public class ReservationUtils {
    public static Reservation getReservation(Long id, ReservationRepository repository) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
    }
}
