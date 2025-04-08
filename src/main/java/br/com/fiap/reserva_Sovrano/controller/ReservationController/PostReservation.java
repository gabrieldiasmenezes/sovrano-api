package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class PostReservation {

    @Autowired
    private ReservationRepository repository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CacheEvict(value = "reservations", allEntries = true)
    @Operation(
        tags = "Reservas",
        summary = "Criar nova reserva",
        description = "Cria uma nova reserva e define o status como 'CONFIRMADA'.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public ResponseEntity<Reservation> create(@RequestBody @Valid Reservation reservation) {
        reservation.setStatus(StatusReserva.CONFIRMADA);
        repository.save(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }
}
