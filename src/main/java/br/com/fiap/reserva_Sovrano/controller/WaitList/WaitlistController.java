package br.com.fiap.reserva_Sovrano.controller.WaitList;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.service.WaitlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/waitlist")
@Tag(name = "Waitlist", description = "Gerenciamento da fila de espera do restaurante")
@SecurityRequirement(name = "bearerAuth")
public class WaitlistController {

    @Autowired
    private WaitlistService waitlistService;

    @Operation(
        summary = "Adicionar cliente à lista de espera",
        description = "Coloca o cliente na fila de espera informando o userId, número de pessoas e período (ALMOÇO ou JANTAR)."
    )
    @PostMapping("/join")
    public ResponseEntity<?> join(
            @RequestParam Long userId,
            @RequestParam int peopleCount,
            @RequestParam Period period
    ) {
        return ResponseEntity.ok(
                waitlistService.joinWaitlist(userId, peopleCount, period)
        );
    }

    @Operation(
        summary = "Admin - visualizar lista de espera",
        description = "Retorna todos os clientes em lista de espera de acordo com a data e período selecionados."
    )
    @GetMapping("/admin")
    public ResponseEntity<?> adminView(
            @RequestParam LocalDate date,
            @RequestParam Period period
    ) {
        return ResponseEntity.ok(
                waitlistService.getWaitlist(date, period)
        );
    }
}
