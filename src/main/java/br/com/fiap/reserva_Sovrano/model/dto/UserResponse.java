package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.UserRole;

public record UserResponse(
    String name, 
    String email,
    String phone,
    UserRole role
) {}
