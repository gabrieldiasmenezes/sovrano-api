package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.WaitlistStatus;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyWaitlistDTO {

    private Long id;
    private LocalDate date;
    private Period period;
    private int peopleCount;
    private WaitlistStatus status;
    private int position;
}
