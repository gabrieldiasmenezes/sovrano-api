package br.com.fiap.reserva_Sovrano.controller.AccountController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/accounts")
public class PostAccount {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private AccountRepository repository;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(
        tags = "Usuários",
        summary = "Cadastrar novo usuário",
        description = "Cria um novo usuário no sistema com os dados fornecidos.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou incompletos")
        }
    )
    public ResponseEntity<Account> create(@RequestBody Account account) {
        log.info("Cadastrando usuário: " + account.getName());
        repository.save(account);
        return ResponseEntity.status(201).body(account);
    }
    
}
