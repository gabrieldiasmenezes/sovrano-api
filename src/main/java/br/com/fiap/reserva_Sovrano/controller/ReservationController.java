package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReservationRepository repository;

    @GetMapping
    @Cacheable("reservations")
    @Operation(
        tags = "Reservas",
        summary = "Listar todas as reservas",
        description = "Retorna uma lista com todas as reservas cadastradas no sistema."
    )
    public List<Reservation> index() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(
        tags = "Reservas",
        summary = "Criar nova reserva",
        description = "Cria uma nova reserva com os dados fornecidos e define o status como 'CONFIRMADA'.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompletos")
        }
    )
    @CacheEvict(value = "reservations", allEntries = true)
    public ResponseEntity<Reservation> create(@RequestBody @Valid Reservation reservation) {
        log.info("Criando reserva: {}", reservation);
        reservation.setStatus(StatusReserva.CONFIRMADA);
        repository.save(reservation);
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping("{idReserva}")
    @Operation(
        tags = "Reservas",
        summary = "Buscar reserva por ID",
        description = "Retorna os dados de uma reserva específica, com base no ID informado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    public Reservation get(@PathVariable Long idReserva) {
        log.info("Buscando reserva ID: {}", idReserva);
        return getReservation(idReserva);
    }

    @DeleteMapping("{idReserva}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        tags = "Reservas",
        summary = "Cancelar reserva",
        description = "Altera o status de uma reserva existente para 'CANCELADA', sem excluí-la do sistema.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    @CacheEvict(value = "reservations", allEntries = true)
    public void cancel(@PathVariable Long idReserva) {
        log.info("Cancelando reserva ID: {}", idReserva);
        Reservation reservation = getReservation(idReserva);
        reservation.setStatus(StatusReserva.CANCELADA);
        repository.save(reservation);
    }

    @DeleteMapping("excluir/{idReserva}")
    @Operation(
        tags = "Reservas",
        summary = "Excluir reserva",
        description = "Remove permanentemente uma reserva do banco de dados.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Reserva excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    @CacheEvict(value = "reservations", allEntries = true)
    public void delete(@PathVariable Long idReserva) {
        log.info("Excluindo reserva ID: {}", idReserva);
        Reservation reservation = getReservation(idReserva);
        repository.delete(reservation);
    }

    @PutMapping("{idReserva}")
    @Operation(
        tags = "Reservas",
        summary = "Atualizar dados da reserva",
        description = "Atualiza os dados de uma reserva existente com base no ID informado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    @CacheEvict(value = "reservations", allEntries = true)
    public Reservation update(@PathVariable Long idReserva, @RequestBody Reservation reservation) {
        log.info("Atualizando reserva ID: {}", idReserva);
        reservation.setIdReserva(idReserva);
        repository.save(reservation);
        return reservation;
    }

    private Reservation getReservation(Long idReserva) {
        return repository.findById(idReserva).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não encontrada"));
    }
}
