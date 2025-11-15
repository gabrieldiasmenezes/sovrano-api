package br.com.fiap.reserva_Sovrano.controller.Reservation;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.service.Reservation.ReservationService;
import br.com.fiap.reserva_Sovrano.specifications.ReservationSpecifications;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationUtils reservationUtils;


    // -------------------------------
    // DTO de filtros para paginação
    // -------------------------------
    public record ReservationFilter(
            Long userId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            Integer people
    ) {}

    // -----------------------------------------------------------
    // GET com paginação + filtros (melhor endpoint para o admin)
    // -----------------------------------------------------------
    @GetMapping
    public Page<ReservationResponseDTO> findAll(ReservationFilter filters, Pageable pageable) {
        var specification = ReservationSpecifications.withFilters(filters);
        return reservationRepository.findAll(specification, pageable)
            .map(reservationUtils :: toDTO);
    }

    // Buscar reservas por usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservations>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findByUser(userId));
    }

    // Buscar por mesa
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Reservations>> findByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(reservationService.listByTable(tableId));
    }

    // Criar reserva
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> create(@Valid @RequestBody Reservations reservation) {
        return ResponseEntity.ok(reservationUtils.toDTO(
            reservationService.create(reservation)
        ));
    }

    // Confirmar
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservations> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirm(id));
    }

    // Cancelar
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/complete")
    public ReservationResponseDTO completeReservation(@PathVariable Long id) {
        return reservationUtils.toDTO(reservationService.completeReservation(id));
    }

    @PutMapping("/{id}")
        public ResponseEntity<ReservationResponseDTO> update(
                @PathVariable Long id,
                @RequestBody Reservations data
        ) {
            Reservations updated = reservationService.update(id, data);
            return ResponseEntity.ok(reservationUtils.toDTO(updated));
    }

    // Deletar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
