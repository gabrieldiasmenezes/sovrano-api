package br.com.fiap.reserva_Sovrano.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDto(

    @NotBlank
    String currentPassword,

    @NotBlank
    @Size(min = 6)
    String newPassword
) {}

