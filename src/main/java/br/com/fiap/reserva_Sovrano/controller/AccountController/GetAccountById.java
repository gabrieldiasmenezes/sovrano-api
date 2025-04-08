package br.com.fiap.reserva_Sovrano.controller.AccountController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.components.AccountUtils;
import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/accounts")
public class GetAccountById {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository repository;
    
    @GetMapping("{id}")
    @Operation(
        tags = "Usuários",
        summary = "Buscar usuário por ID",
        description = "Retorna os dados de um usuário específico, identificado pelo seu ID.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public Account get(@PathVariable Long id) {
        Account account = AccountUtils.getAccount(id, repository);
        log.info("Buscando usuário ID: " + id);
        return account;
    }
    
}
