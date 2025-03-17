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


@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final Logger log=LoggerFactory.getLogger(getClass());
    //Injecao de dependencias
    @Autowired
    private AccountRepository repository;
    
    @GetMapping
    public List<Account> index(){
        return repository.findAll();
    }
    @PostMapping
    @ResponseStatus(code=HttpStatus.CREATED)
    public ResponseEntity<Account> create(@RequestBody Account account){
        log.info("Cadastrando usuário "+account.getName());
        repository.save(account);
        return ResponseEntity.status(201).body(account);
    }
    @GetMapping("{id}")
    public Account get(@PathVariable Long id){
        log.info("buscando usuario "+id);
        return getaccount(id);
    }
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void destroy(@PathVariable Long id){
        log.info("Deletando usuário "+id);
        repository.delete(getaccount(id));
    }
    @PutMapping("{id}")
    public Account update(@PathVariable Long id,@RequestBody Account account){
        log.info("Atualizando informações da conta "+id);
        account.setId(id);
        repository.save(account);
        return account;
    }
    private Account getaccount(Long id){
        return repository.findById(id).
        orElseThrow(
            ()->new ResponseStatusException
            (HttpStatus.NOT_FOUND)
        );
    }
}
