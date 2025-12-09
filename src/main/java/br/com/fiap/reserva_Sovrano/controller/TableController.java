package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.service.TablesService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/tables")
@Tag(name = "Tables", description = "Gerenciamento das mesas do restaurante")
@SecurityRequirement(name = "bearerAuth")  // 🔒 Todos os endpoints exigem token
public class TableController {

    @Autowired
    private TablesService tableService;

    @GetMapping
    @Operation(
        summary = "Listar todas as mesas",
        description = "Retorna todas as mesas cadastradas no sistema.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
        }
    )
    public ResponseEntity<List<Tables>> listAll() {
        return ResponseEntity.ok(tableService.listAll());
    }

    @PostMapping
    @Operation(
        summary = "Cadastrar mesa",
        description = "Cria uma nova mesa. Apenas administradores devem ter acesso a este endpoint.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mesa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou faltando"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — somente ADMIN")
        }
    )
    public ResponseEntity<Tables> create(@Valid @RequestBody Tables table) {
        return ResponseEntity.ok(tableService.create(table));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar mesa",
        description = "Edita os dados de uma mesa (ex: capacidade, disponibilidade). Apenas ADMIN.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mesa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — somente ADMIN"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada")
        }
    )
        public ResponseEntity<Tables> updateAvailability(
            @PathVariable Long id,
            @RequestBody Tables table
        ) {
        return ResponseEntity.ok(tableService.update(id, table));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir mesa",
        description = "Remove uma mesa do sistema. Apenas administradores têm permissão.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Mesa excluída com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Acesso negado — somente ADMIN"),
            @ApiResponse(responseCode = "404", description = "Mesa não encontrada")
        }
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
