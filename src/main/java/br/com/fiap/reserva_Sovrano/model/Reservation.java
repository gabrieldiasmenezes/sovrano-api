package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;
    private LocalDateTime reservationDate;
    private int quantPessoas;
    private StatusReserva status=StatusReserva.CONFIRMADA;
    
}
