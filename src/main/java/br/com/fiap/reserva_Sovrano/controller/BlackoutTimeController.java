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


@RestController
@RequestMapping("/blackouts")
public class BlackoutTimeController {

    @Autowired
    private BlackoutTimeService service;

    @PostMapping
    public ResponseEntity<BlackoutTime> create(@RequestBody BlackoutTimeRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<BlackoutTime>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
