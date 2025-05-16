package br.com.fiap.reserva_Sovrano.controller.ReservationController;

import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.Authentication;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class GetReservationByUserEmail {

    @Autowired
    private ReservationRepository repository;

    @GetMapping("/me")
    @Operation(
        tags = "Reservas",
        summary = "Listar minhas reservas",
        description = "Retorna todas as reservas feitas pelo usuário logado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Reservas retornadas com sucesso")
        }
    )
    public List<Reservation> getMyReservations(Authentication authentication) {
        String email = authentication.getName(); 
        System.out.println(email);

        return repository.findByAccount_Email(email);
    }
}
