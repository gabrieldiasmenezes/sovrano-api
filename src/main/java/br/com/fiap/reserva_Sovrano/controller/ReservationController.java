package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.service.ReservationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservations>> listAll() {
        return ResponseEntity.ok(reservationService.listAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservations>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findByUser(userId));
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Reservations>> findByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(reservationService.listByTable(tableId));
    }

    @PostMapping
    public ResponseEntity<Reservations> create(@Valid @RequestBody Reservations reservation) {
        return ResponseEntity.ok(reservationService.create(reservation));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservations> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirm(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservations> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
