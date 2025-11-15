package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.service.TablesService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tables")
public class TableController {

    @Autowired
    private TablesService tableService;

    @GetMapping
    public ResponseEntity<List<Tables>> listAll() {
        return ResponseEntity.ok(tableService.listAll());
    }

    @PostMapping
    public ResponseEntity<Tables> create(@Valid @RequestBody Tables table) {
        return ResponseEntity.ok(tableService.create(table));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tables> updateAvailability( @Valid @PathVariable Long id, @RequestBody Tables table) {
        return ResponseEntity.ok(tableService.update(id, table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
