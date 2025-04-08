package br.com.fiap.reserva_Sovrano.controller.AccountController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.dto.LoginDTO;
import br.com.fiap.reserva_Sovrano.model.Account;
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
        log.info("Autenticando um usuário com base no email e senha fornecidos.");
        Account account = repository.findByEmail(loginDTO.email());

        if (account == null || !account.getPassword().equals(loginDTO.password())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado ou senha incorreta"));
        }

        return ResponseEntity.ok(account);
}

    
}
