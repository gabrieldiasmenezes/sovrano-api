package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.Period;

public record JoinWaitlistRequest(
        Long userId,
        int peopleCount,
        Period period,
        boolean hasLegalPriority,
        String legalReason
) {}
