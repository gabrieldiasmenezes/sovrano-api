package br.com.fiap.reserva_Sovrano.controller.Reservation;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.service.Reservation.ReservationService;
import br.com.fiap.reserva_Sovrano.specifications.ReservationSpecifications;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 📌 IMPORTAÇÕES DO SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@Tag(
        name = "Reservations - Admin",
        description = "Endpoints exclusivos para funcionários (ADMIN) gerenciarem reservas feitas por clientes via telefone."
)
@SecurityRequirement(name = "bearerAuth") // 🔐 JWT obrigatório
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

        @Autowired
        private ReservationRepository reservationRepository;

        @Autowired
        private br.com.fiap.reserva_Sovrano.repository.UserRepository userRepository;

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
    // GET com paginação + filtros (principal endpoint para admin)
    // -----------------------------------------------------------
    @Operation(
            summary = "Lista reservas com filtros e paginação",
            description = """
                    Endpoint exclusivo para ADMIN.

                    Permite filtrar reservas por usuário, data, horário e quantidade de pessoas.
                    Ideal para relatórios e dashboards internos do restaurante.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado (somente ADMIN)")
    })
        @GetMapping
        public Page<ReservationResponseDTO> findAll(@org.springframework.web.bind.annotation.ModelAttribute ReservationFilter filters, Pageable pageable) {
        var specification = ReservationSpecifications.withFilters(filters);
        return reservationRepository.findAll(specification, pageable)
            .map(reservationUtils :: toDTO);
    }

    @Operation(
            summary = "Lista reservas de um usuário",
            description = "Consulta todas as reservas associadas a um cliente específico. Apenas ADMIN pode acessar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
        @GetMapping("/user/{email}")
        public ResponseEntity<List<Reservations>> findByUser(@PathVariable String email) {
                var user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
                return ResponseEntity.ok(reservationService.findByUser(user.getId()));
        }

    @Operation(
            summary = "Lista reservas por mesa",
            description = "Busca todas as reservas associadas a uma mesa específica. Somente ADMIN pode acessar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reservas encontradas"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Reservations>> findByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(reservationService.listByTable(tableId));
    }

    @Operation(
            summary = "Cria uma nova reserva",
            description = """
                    Criação manual de reserva feita pelo funcionário (ADMIN)
                    após ligação do cliente. Não exige que o cliente tenha conta.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
        @PostMapping
        public ResponseEntity<ReservationResponseDTO> create(@RequestBody Reservations reservation) {
                return ResponseEntity.ok(reservationUtils.toDTO(
                        reservationService.create(reservation)
                ));
        }

    @Operation(
            summary = "Confirma uma reserva",
            description = "Usado pelo ADMIN para confirmar manualmente uma reserva."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva confirmada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Reservations> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirm(id));
    }

    @Operation(
            summary = "Cancela uma reserva",
            description = "Cancela uma reserva existente. Operação permitida apenas para ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Completa uma reserva",
            description = "Marca a reserva como concluída. Usado após o cliente ser atendido."
    )
    @PatchMapping("/{id}/complete")
    public ReservationResponseDTO completeReservation(@PathVariable Long id) {
        return reservationUtils.toDTO(reservationService.completeReservation(id));
    }

    @Operation(
            summary = "Atualiza informações da reserva",
            description = "Permite editar qualquer campo de uma reserva. Exclusivo para ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva atualizada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> update(
            @PathVariable Long id,
            @RequestBody Reservations data
    ) {
        Reservations updated = reservationService.update(id, data);
        return ResponseEntity.ok(reservationUtils.toDTO(updated));
    }

    @Operation(
            summary = "Deleta uma reserva",
            description = "Remove permanentemente uma reserva. Apenas ADMIN tem permissão."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva deletada"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
