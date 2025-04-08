package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.components.ReservationUtils;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class GetReservationById {

    @Autowired
    private ReservationRepository repository;

    @GetMapping("{idReserva}")
    @Operation(
        tags = "Reservas",
        summary = "Buscar reserva por ID",
        description = "Retorna os dados de uma reserva específica.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    public Reservation findById(@PathVariable Long idReserva) {
        return ReservationUtils.getReservation(idReserva, repository);
    }
}
