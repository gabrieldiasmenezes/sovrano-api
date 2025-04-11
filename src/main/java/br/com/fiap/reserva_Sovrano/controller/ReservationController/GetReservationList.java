package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.specifications.ReservationSpecifications;
import io.swagger.v3.oas.annotations.Operation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservations")
public class GetReservationList {
    public record ReservationFilter(String description,LocalDate date,Integer qnt){}

    @Autowired
    private ReservationRepository repository;

    @GetMapping
    @Cacheable("reservations")
    @Operation(
        tags = "Reservas",
        summary = "Listar todas as reservas",
        description = "Retorna uma lista com todas as reservas cadastradas no sistema."
    )
    public Page<Reservation> findAll(ReservationFilter filters,Pageable pageble) {
        var specification=ReservationSpecifications.withFilters(filters);
        return repository.findAll(specification,pageble);
    }
}
