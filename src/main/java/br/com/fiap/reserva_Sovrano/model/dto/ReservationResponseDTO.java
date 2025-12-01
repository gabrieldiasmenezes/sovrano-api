package br.com.fiap.reserva_Sovrano.model.dto;

import java.time.LocalDateTime;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long id;
    private LocalDateTime reservationDateTime;
    private Integer peopleCount;
    private StatusReservation status;
    private String userEmail; 
    private Long tableId;
}
