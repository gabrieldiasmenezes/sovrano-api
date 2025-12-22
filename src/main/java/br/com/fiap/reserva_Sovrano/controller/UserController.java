package br.com.fiap.reserva_Sovrano.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.dto.UserDto;
import br.com.fiap.reserva_Sovrano.model.dto.UserPasswordUpdateDto;
import br.com.fiap.reserva_Sovrano.model.dto.UserResponse;
import br.com.fiap.reserva_Sovrano.model.dto.UserUpdateDto;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.service.UserService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Gerenciamento de usuários")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ----------------------------------------------------------------------
    // GET /users/me - Dados do usuário autenticado
    // ----------------------------------------------------------------------
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Obter dados do usuário logado",
        description = "Retorna nome, email, telefone e informações de prioridade do usuário autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public ResponseEntity<UserResponse> getMe(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email)
                .map(user -> ResponseEntity.ok(
                        new UserResponse(
                            user.getName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getRole()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------------
    // PUT /users/me - Atualizar dados do usuário autenticado
    // ----------------------------------------------------------------------
    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Atualizar dados do usuário logado",
        description = "Permite alterar nome, telefone e dados de prioridade. Email não pode ser alterado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
        }
    )
    public ResponseEntity<UserResponse> updateMe(
            @Valid @RequestBody UserUpdateDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();

        Users updatedUser = userService.update(email, dto);

        return ResponseEntity.ok(
            new UserResponse(
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getRole()
            )
        );
    }

    @PutMapping("/me/password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Alterar senha do usuário logado",
        description = "Exige a senha atual para confirmação.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
        }
    )
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UserPasswordUpdateDto dto,
            Authentication authentication
    ) {
        String email = authentication.getName();

        userService.updatePassword(email, dto);

        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------------
    // DELETE /users/me - Deletar conta do usuário autenticado
    // ----------------------------------------------------------------------
    @DeleteMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Excluir conta do usuário logado",
        description = "Permite que o usuário autenticado delete permanentemente sua conta.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
        }
    )
    public ResponseEntity<Void> deleteMe(Authentication authentication) {
        String email = authentication.getName();
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------------
    // POST /users - Registro de um novo usuário (público)
    // ----------------------------------------------------------------------
    @PostMapping
    @Operation(
        summary = "Criar novo usuário (registro)",
        description = "Endpoint público para criar uma conta no sistema. Não requer token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserDto request) {
        Users user = Users.builder()
            .name(request.name())
            .email(request.email())
            .password(request.password())
            .phone(request.phone())
            .build();
        Users createdUser = userService.create(user);

        return ResponseEntity.ok(
                new UserResponse(
                    createdUser.getName(),
                    createdUser.getEmail(),
                    createdUser.getPhone(),
                    createdUser.getRole()
                )
        );
    }

    @PostMapping("/admin")
    @Operation(
        summary = "Criar novo usuário (registro)",
        description = "Endpoint público para criar uma conta no sistema. Não requer token.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody UserDto request) {
        Users user = Users.builder()
            .name(request.name())
            .email(request.email())
            .password(request.password())
            .phone(request.phone())
            .build();
        Users createdUser = userService.createAdmin(user);

        return ResponseEntity.ok(
                new UserResponse(
                    createdUser.getName(),
                    createdUser.getEmail(),
                    createdUser.getPhone(),
                    createdUser.getRole()
                )
        );
    }

    // ----------------------------------------------------------------------
    // PATCH /users/{id}/unblock - Desbloquear usuário (ADMIN)
    // ----------------------------------------------------------------------
    @PatchMapping("/{id}/unblock")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Desbloquear usuário",
        description = "Endpoint administrativo para remover bloqueio de um usuário.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário desbloqueado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — somente ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
        }
    )
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        userService.desblockUser(user);

        return ResponseEntity.ok("Usuário desbloqueado com sucesso.");
    }
}
