package br.com.fiap.reserva_Sovrano.model.dto;

import java.time.LocalDateTime;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class BlackoutTimeRequestDTO {

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    @Size(max = 200)
    private String reason;
}