package br.com.fiap.reserva_Sovrano.controller;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final Logger log = LoggerFactory.getLogger(getClass());

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
        log.info("Buscando usuário ID: " + id);
        return getaccount(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        tags = "Usuários",
        summary = "Remover usuário",
        description = "Remove permanentemente um usuário do sistema com base no ID informado.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public void destroy(@PathVariable Long id) {
        log.info("Deletando usuário ID: " + id);
        repository.delete(getaccount(id));
    }

    @PutMapping("{id}")
    @Operation(
        tags = "Usuários",
        summary = "Atualizar dados do usuário",
        description = "Atualiza os dados de um usuário existente com as informações fornecidas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public Account update(@PathVariable Long id, @RequestBody Account account) {
        log.info("Atualizando dados do usuário ID: " + id);
        account.setId(id);
        repository.save(account);
        return account;
    }

    private Account getaccount(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
