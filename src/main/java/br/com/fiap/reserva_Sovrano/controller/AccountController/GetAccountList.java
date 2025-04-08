package br.com.fiap.reserva_Sovrano.controller.AccountController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/accounts")
public class GetAccountList {
    @Autowired
    private AccountRepository repository;

    @GetMapping
    @Operation(
        tags = "Usuários",
        summary = "Listar todos os usuários",
        description = "Retorna uma lista completa de usuários cadastrados no sistema."
    )
    public List<Account> index() {
        return repository.findAll();
    }
    
}
