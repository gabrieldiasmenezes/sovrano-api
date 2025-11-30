package br.com.fiap.reserva_Sovrano.controller.Reservation;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.service.Reservation.ReservationCustomerService;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/reservations/me")
@Tag(name = "Reservas (Cliente)", description = "Endpoints para o usuário autenticado gerenciar suas próprias reservas.")
@SecurityRequirement(name = "bearerAuth")
public class ReservationCustomerController {

    @Autowired
    private ReservationCustomerService userService;

    @Autowired
    private ReservationUtils reservationUtils;

    public record ReservationStatusFilter(StatusReservation status) {}

    // -------------------------
    // LISTAR MINHAS RESERVAS
    // -------------------------
    @Operation(
        summary = "Listar minhas reservas",
        description = "Retorna todas as reservas pertencentes ao usuário autenticado. Permite paginação e filtro por status.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente")
        }
    )
    @GetMapping
    public ResponseEntity<?> getMyReservations(
            Authentication auth,
            ReservationStatusFilter filter,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getMyReservations(auth, filter, pageable));
    }

    // -------------------------
    // CRIAR MINHA RESERVA
    // -------------------------
    @Operation(
        summary = "Criar reserva",
        description = "Permite ao usuário autenticado criar uma nova reserva vinculada à sua conta.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou ausente")
        }
    )
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createMyReservation(
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(
            userService.createMyReservation(data, auth)
        ));
    }

    // -------------------------
    // CONFIRMAR MINHA RESERVA
    // -------------------------
    @Operation(
        summary = "Confirmar reserva",
        description = "Permite ao usuário confirmar **somente sua própria reserva**.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva confirmada com sucesso"),
            @ApiResponse(responseCode = "403", description = "O usuário tentou confirmar uma reserva que não é dele"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ReservationResponseDTO> confirmMyReservation(
            @PathVariable Long id,
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(userService.confirmMyReservation(id, auth)));
    }

    // -------------------------
    // CANCELAR MINHA RESERVA
    // -------------------------
    @Operation(
        summary = "Cancelar reserva",
        description = "O usuário pode cancelar **somente suas próprias reservas**.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "403", description = "Tentativa de cancelar reserva de outro usuário"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelMyReservation(
            @PathVariable Long id,
            Authentication auth
    ) {
        userService.cancelMyReservation(id, auth);
        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // ATUALIZAR MINHA RESERVA
    // -------------------------
    @Operation(
        summary = "Atualizar reserva",
        description = "Permite editar uma reserva pertencente ao usuário autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reserva atualizada"),
            @ApiResponse(responseCode = "403", description = "Tentativa de editar uma reserva de outro usuário"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
        }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> updateMyReservation(
            @PathVariable Long id,
            @RequestBody Reservations data,
            Authentication auth
    ) {
        return ResponseEntity.ok(reservationUtils.toDTO(userService.updateMyReservation(id, data, auth)));
    }
}
