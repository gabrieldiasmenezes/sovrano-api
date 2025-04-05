package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;
import java.time.LocalTime;
import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
   @NotBlank(message = "O nome não pode estar em branco")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;

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
    
}
