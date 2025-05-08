package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.UserRole;

public record AccountResponse(Long id,String name,String email,String phone,UserRole role) {}
