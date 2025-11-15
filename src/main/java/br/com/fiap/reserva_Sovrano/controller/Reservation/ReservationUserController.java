package br.com.fiap.reserva_Sovrano.controller.Reservation;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.service.Reservation.ReservationUserService;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservations/me")
public class ReservationUserController {

    @Autowired
    private ReservationUserService userService;

    @GetMapping
    public ResponseEntity<List<Reservations>> getMyReservations(Authentication auth) {
        return ResponseEntity.ok(userService.getMyReservations(auth));
    }

    @PostMapping
    public ResponseEntity<Reservations> createMyReservation(
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.createMyReservation(data, auth));
    }
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservations> confirmMyReservation(
            @PathVariable Long id, 
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.confirmMyReservation(id, auth));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMyReservation(
            @PathVariable Long id, 
            Authentication auth
    ) {
        userService.cancelMyReservation(id, auth);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservations> updateMyReservation(
            @PathVariable Long id,
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(userService.updateMyReservation(id, data, auth));
    }
}