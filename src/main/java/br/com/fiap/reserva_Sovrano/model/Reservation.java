package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;
import java.time.LocalTime;
import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @NotNull(message = "A data da reserva não pode ser nula")
    @FutureOrPresent(message = "A data da reserva deve estar no presente ou no futuro")
    private LocalDate date;

    @NotNull(message = "O horário da reserva não pode ser nulo")
    private LocalTime time;

    @Min(value = 1, message = "A quantidade de pessoas deve ser no mínimo 1")
    @Max(value = 8, message = "A quantidade máxima de pessoas por reserva é 8")
    private int qnt;

    @Builder.Default
    private StatusReserva status=StatusReserva.CONFIRMADA;

    @ManyToOne
    @JoinColumn(name = "id_account")
    @NotNull(message = "A conta não pode ser nula")
    private Account account;
    
}
