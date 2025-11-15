package br.com.fiap.reserva_Sovrano.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.dto.UserResponse;
import br.com.fiap.reserva_Sovrano.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Buscar dados do usuário logado (somente name, email e phone)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(
                        new UserResponse(user.getName(), user.getEmail(), user.getPhone())
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // Atualizar dados do usuário logado (name e phone apenas)
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody Users user, Authentication authentication) {
        String email = authentication.getName();
        Users updatedUser = userService.update(email, user);
        return ResponseEntity.ok(
                new UserResponse(updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPhone())
        );
    }

    // Deletar conta do usuário logado
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(Authentication authentication) {
        String email = authentication.getName();
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    // Criar usuário (registro)
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody Users user) {
        Users createdUser = userService.create(user);
        return ResponseEntity.ok(
                new UserResponse(createdUser.getName(), createdUser.getEmail(), createdUser.getPhone())
        );
    }
}
