package br.com.fiap.reserva_Sovrano.controller.WaitList;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.service.UserService;
import br.com.fiap.reserva_Sovrano.service.WaitlistService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

// 📌 SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/waitlist/me")
@Tag(
        name = "Waitlist - Cliente",
        description = "Endpoints para o cliente consultar sua posição na fila de espera."
)
@SecurityRequirement(name = "bearerAuth") // 🔐 JWT obrigatório
public class WaitlistCustomerController {

    @Autowired
    private WaitlistService waitlistService;

    @Autowired
    private UserService userService;

    // ----------------------------------------------------------
    // LISTA TODAS AS FILAS DO USUÁRIO
    // ----------------------------------------------------------
    @Operation(
            summary = "Lista filas de espera do cliente",
            description = """
                    Retorna todas as entradas do usuário na fila de espera,
                    incluindo sua posição atual em cada período (almoço/jantar)
                    e data correspondente.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Filas retornadas com sucesso")
    @GetMapping
    public ResponseEntity<?> getMyWaitlists(Authentication auth) {

        String email = auth.getName();

        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return ResponseEntity.ok(
                waitlistService.getMyWaitlists(user.getId())
        );
    }

    @Operation(
            summary = "Sair da fila de espera",
            description = "Permite ao cliente remover sua própria entrada na fila de espera."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> leave(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();

        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        waitlistService.leaveWaitlist(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------
    // POSIÇÃO DO CLIENTE EM UMA DATA E PERÍODO
    // ----------------------------------------------------------
    @Operation(
            summary = "Retorna posição do cliente na fila",
            description = """
                    Consulta a posição do usuário em uma fila específica,
                    filtrando por data e período (ALMOÇO ou JANTAR).
                    """
    )
    @ApiResponse(responseCode = "200", description = "Posição retornada com sucesso")
    @GetMapping("/position")
    public ResponseEntity<?> getMyPosition(
            Authentication auth,
            @RequestParam Period period,
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
            @RequestParam LocalDate date
    ) {
        String email = auth.getName();

        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        return ResponseEntity.ok(
                waitlistService.getUserPosition(user.getId(), period, date)
        );
    }
}
