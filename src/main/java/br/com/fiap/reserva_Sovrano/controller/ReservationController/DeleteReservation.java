package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.components.ReservationUtils;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class DeleteReservation {

    @Autowired
    private ReservationRepository repository;

    @DeleteMapping("excluir/{idReserva}")
    @CacheEvict(value = "reservations", allEntries = true)
    @Operation(
        tags = "Reservas",
        summary = "Excluir reserva",
        description = "Remove permanentemente uma reserva do banco de dados.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Reserva excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    public void delete(@PathVariable Long idReserva) {
        Reservation reservation = ReservationUtils.getReservation(idReserva, repository);
        repository.delete(reservation);
    }
}
