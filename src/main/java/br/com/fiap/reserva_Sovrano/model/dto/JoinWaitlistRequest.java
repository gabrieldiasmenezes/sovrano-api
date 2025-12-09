package br.com.fiap.reserva_Sovrano.model.dto;

import br.com.fiap.reserva_Sovrano.components.Period;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record JoinWaitlistRequest(
        @NotNull
        Long userId,

        @Min(1)
        int peopleCount,

        @NotNull
        Period period,

        boolean hasLegalPriority,
        String legalReason
) {}
