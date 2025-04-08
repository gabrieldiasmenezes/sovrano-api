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
public class PutReservation {

    @Autowired
    private ReservationRepository repository;

    @PutMapping("{idReserva}")
    @CacheEvict(value = "reservations", allEntries = true)
    @Operation(
        tags = "Reservas",
        summary = "Atualizar dados da reserva",
        description = "Atualiza os dados de uma reserva existente com base no ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public Reservation update(@PathVariable Long idReserva, @RequestBody Reservation reservation) {
        Reservation existing = ReservationUtils.getReservation(idReserva, repository);
        reservation.setIdReserva(existing.getIdReserva());
        return repository.save(reservation);
    }
}
