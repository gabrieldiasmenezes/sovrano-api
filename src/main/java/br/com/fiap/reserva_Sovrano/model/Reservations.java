package br.com.fiap.reserva_Sovrano.model;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Reservation date and time are required.")
    private LocalDateTime reservationDateTime;

    @NotNull(message = "Number of people is required.")
    @Min(value = 1, message = "Minimum 1 person required.")
    @Max(value = 8, message = "Maximum 8 people per reservation.")
    private Integer peopleCount;

    @NotNull(message = "Reservation status is required.")
    @Enumerated(EnumType.STRING)
    private StatusReservation status;

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotNull(message = "Table ID is required.")
    private Long tableId;
}
