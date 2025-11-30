package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.BlackoutTime;
import br.com.fiap.reserva_Sovrano.model.dto.BlackoutTimeRequestDTO;
import br.com.fiap.reserva_Sovrano.service.BlackoutTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/blackouts")
@Tag(name = "Blackout Times", description = "Gerenciamento de horários bloqueados para eventos privados")
@SecurityRequirement(name = "bearerAuth") // 🔒 todos os endpoints exigem token
public class BlackoutTimeController {

    @Autowired
    private BlackoutTimeService service;

    @PostMapping
    @Operation(
        summary = "Criar horário bloqueado",
        description = "Cria um novo período de blackout (horário indisponível para reservas). Apenas administradores podem realizar esta ação.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Horário de blackout criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — não é administrador")
        }
    )
    public ResponseEntity<BlackoutTime> create(@RequestBody BlackoutTimeRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    @Operation(
        summary = "Listar horários bloqueados",
        description = "Retorna todos os horários de blackout cadastrados.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
        }
    )
    public ResponseEntity<List<BlackoutTime>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir horário bloqueado",
        description = "Remove um horário de blackout pelo ID. Apenas administradores podem realizar esta ação.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Horário excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — não é administrador"),
            @ApiResponse(responseCode = "404", description = "Blackout não encontrado")
        }
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
