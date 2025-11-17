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

@RestController
@RequestMapping("/waitlist/me")
public class WaitlistCustomerController {

    @Autowired
    private WaitlistService waitlistService;

    @Autowired
    private UserService userService;

    // 📌 Lista todas as filas do usuário com posição
    @GetMapping
    public ResponseEntity<?> getMyWaitlists(Authentication auth) {

        String email = auth.getName();

        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return ResponseEntity.ok(
                waitlistService.getMyWaitlists(user.getId())
        );
    }

    // 📌 Ver posição específica (se quiser)
    @GetMapping("/position")
    public ResponseEntity<?> getMyPosition(
            Authentication auth,
            @RequestParam Period period,
            @RequestParam LocalDate date
    ) {
        String email = auth.getName();

        Users user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return ResponseEntity.ok(
                waitlistService.getUserPosition(user.getId(), period, date)
        );
    }
}