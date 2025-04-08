package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class GetReservationList {

    @Autowired
    private ReservationRepository repository;

    @GetMapping
    @Cacheable("reservations")
    @Operation(
        tags = "Reservas",
        summary = "Listar todas as reservas",
        description = "Retorna uma lista com todas as reservas cadastradas no sistema."
    )
    public List<Reservation> findAll() {
        return repository.findAll();
    }
}
