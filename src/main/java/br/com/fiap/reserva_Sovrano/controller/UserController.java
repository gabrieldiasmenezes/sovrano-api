package br.com.fiap.reserva_Sovrano.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.User;



@RestController
public class UserController {
    private List<User> repository=new ArrayList<>();
    @GetMapping(path = "/users")
    public List<User> index(){
        return repository;
    }
    @PostMapping(path="/users")
    @ResponseStatus(code=HttpStatus.CREATED)
    public ResponseEntity<User> create(@RequestBody User user){
        System.out.println("Cadastrando usuário "+user.getName());
        repository.add(user);
        return ResponseEntity.status(201).body(user);
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> get(@PathVariable Long id){
        System.out.println("buscando usuario "+id);
        var user=repository.stream()
            .filter(u->u.getId().equals(id))
            .findFirst();
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(user.get());
    }
    
}
