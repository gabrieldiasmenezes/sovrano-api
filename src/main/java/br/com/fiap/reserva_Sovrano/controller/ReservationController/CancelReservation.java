package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.components.ReservationUtils;
import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class CancelReservation {

    @Autowired
    private ReservationRepository repository;

    @DeleteMapping("{idReserva}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "reservations", allEntries = true)
    @Operation(
        tags = "Reservas",
        summary = "Cancelar reserva",
        description = "Altera o status de uma reserva existente para 'CANCELADA'.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    public void cancel(@PathVariable Long idReserva) {
        Reservation reservation = ReservationUtils.getReservation(idReserva, repository);
        reservation.setStatus(StatusReserva.CANCELADA);
        repository.save(reservation);
    }
}

