package br.com.fiap.reserva_Sovrano.model.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateDto(
    @Size(max = 100)
    String name,

    @Size(max = 20)
    String phone
) {
    
}
