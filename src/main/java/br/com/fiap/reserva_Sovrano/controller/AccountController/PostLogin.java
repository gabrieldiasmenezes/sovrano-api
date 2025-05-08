package br.com.fiap.reserva_Sovrano.controller.AccountController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.dto.LoginDTO;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class PostLogin {
    
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
@Operation(
    tags = "Usuários",
    summary = "Login do usuário",
    description = "Autentica um usuário com base no email e senha fornecidos.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    }
)
public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {
    log.info("Tentando login com email: {}", loginDTO.email());

    return repository.findByEmail(loginDTO.email())
        .map(account -> {
            if (passwordEncoder.matches(loginDTO.password(), account.getPassword())) {
                return ResponseEntity.ok(account);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Senha incorreta"));
            }
        })
        .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Usuário não encontrado")));
}
}
