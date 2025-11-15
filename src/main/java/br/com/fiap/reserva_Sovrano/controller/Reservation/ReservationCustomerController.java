package br.com.fiap.reserva_Sovrano.controller.Reservation;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.service.Reservation.ReservationCustomerService;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/reservations/me")
public class ReservationCustomerController {

    @Autowired
    private ReservationCustomerService userService;

    @Autowired
    private ReservationUtils reservationUtils;


    public record ReservationStatusFilter(StatusReservation status) {}

    @GetMapping
    public ResponseEntity<?> getMyReservations(
            Authentication auth,
            ReservationStatusFilter filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getMyReservations(auth, filter, pageable));
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createMyReservation(
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(
            userService.createMyReservation(data, auth)
        ));
    }
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponseDTO> confirmMyReservation(
            @PathVariable Long id, 
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(userService.confirmMyReservation(id, auth)));
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
    public ResponseEntity<ReservationResponseDTO> updateMyReservation(
            @PathVariable Long id,
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(userService.updateMyReservation(id, data, auth)));
    }
}