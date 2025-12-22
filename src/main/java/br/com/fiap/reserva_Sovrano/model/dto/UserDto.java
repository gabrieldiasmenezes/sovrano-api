package br.com.fiap.reserva_Sovrano.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDto(
    @NotBlank String name,
    @Email String email,
    @NotBlank String password,
    String phone
) {} 
