package br.com.fiap.reserva_Sovrano.utils;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class GlobalUtils {

    public static <T> T getOrThrow(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new IllegalArgumentException(message));
    }

    public static void check(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public void validateLegalPriority(boolean hasPriority, String reason) {
        if (hasPriority) {
            GlobalUtils.check(reason == null || reason.isBlank(),
                "Você deve informar o motivo da prioridade (idoso, gestante ou PCD).");
        }
    }
    
}
