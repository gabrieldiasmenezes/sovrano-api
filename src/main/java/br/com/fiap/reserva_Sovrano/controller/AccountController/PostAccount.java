package br.com.fiap.reserva_Sovrano.controller.AccountController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.model.dto.AccountResponse;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class PostAccount {
    
    @Autowired
    private AccountRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
    public AccountResponse create(@RequestBody @Valid Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        var userSaved=repository.save(account);
        return new AccountResponse(userSaved.getId(), userSaved.getName(), userSaved.getEmail(), userSaved.getPhone(), userSaved.getRole());
    }
    
}
