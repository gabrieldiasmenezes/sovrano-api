package br.com.fiap.reserva_Sovrano.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tables")
public class Tables {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Table capacity is required.")
    @Min(value = 2, message = "Minimum capacity is 2 people.")
    @Max(value = 6, message = "Maximum capacity is 6 people.")
    private Integer capacity;

    // 👇 ADICIONAR ESTES CAMPOS
    @NotNull(message = "X coordinate is required.")
    private Integer posX;

    @NotNull(message = "Y coordinate is required.")
    private Integer posY;
}
