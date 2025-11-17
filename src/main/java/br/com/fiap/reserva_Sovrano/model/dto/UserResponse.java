package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.PriorityType;

public record UserResponse(
    String name, 
    String email,
    String phone,
    PriorityType priorityType,
    String priorityReason
     
) {}
