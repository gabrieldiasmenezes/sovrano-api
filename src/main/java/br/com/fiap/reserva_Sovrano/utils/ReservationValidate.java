package br.com.fiap.reserva_Sovrano.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.service.BlackoutTimeService;

@Component
public class ReservationValidate {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BlackoutTimeService blackoutService;



    // =======================================================
    // FUNÇÃO ÚNICA → valida tudo que é comum
    // =======================================================
    public void validateCreateOrUpdate(Integer peopleCount, Long tableId, LocalDateTime dateTime) {

        validateFutureDate(dateTime);
        validateBlackout(dateTime);
        validateRestaurantHours(dateTime);

        Tables table = validateTableExists(tableId);

        validateTableCapacity(table, peopleCount);
        validateAvailableTablesForPeople(peopleCount, dateTime);
        validateTableAvailability(tableId, dateTime);
    }



    // =======================================================
    // 1. Garantir que a mesa existe
    // =======================================================
    public Tables validateTableExists(Long tableId) {
        return tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Mesa não encontrada."));
    }



    // =======================================================
    // 2. DATA FUTURA
    // =======================================================
    public void validateFutureDate(LocalDateTime dateTime) {
        GlobalUtils.check(dateTime.isBefore(LocalDateTime.now()),
                "A reserva precisa ser feita para uma data futura.");
    }



    // =======================================================
    // 3. BLACKOUT TIMES
    // =======================================================
    public void validateBlackout(LocalDateTime dateTime) {
        GlobalUtils.check(
                blackoutService.isInBlackout(dateTime),
                "Não é possível reservar nesse horário. O restaurante estará ocupado com um evento interno."
        );
    }



    // =======================================================
    // 4. CAPACIDADE DA MESA
    // =======================================================
    public void validateTableCapacity(Tables table, Integer peopleCount) {
        if (peopleCount == null) {
            throw new IllegalArgumentException("A quantidade de pessoas é obrigatória.");
        }

        if (peopleCount > table.getCapacity()) {
            throw new IllegalStateException(
                "A mesa selecionada suporta apenas " + table.getCapacity() + " pessoas."
            );
        }
    }



    // =======================================================
    // 5. MESAS DISPONÍVEIS
    // =======================================================
    public void validateAvailableTablesForPeople(Integer peopleCount, LocalDateTime dateTime) {

        List<Tables> compatibleTables =
                tableRepository.findByCapacityGreaterThanEqual(peopleCount);

        GlobalUtils.check(
                compatibleTables.isEmpty(),
                "Não existe nenhuma mesa com capacidade para " + peopleCount + " pessoas."
        );

        boolean hasAvailable = compatibleTables.stream().anyMatch(table ->
                !reservationRepository.existsByTableIdAndReservationDateTimeAndStatus(
                        table.getId(),
                        dateTime,
                        StatusReservation.CONFIRMED
                )
        );

        GlobalUtils.check(
                !hasAvailable,
                "Não há mesas disponíveis para " + peopleCount + " pessoas neste horário."
        );
    }



    // =======================================================
    // 6. DISPONIBILIDADE DA MESA ESPECÍFICA
    // =======================================================
    public void validateTableAvailability(Long tableId, LocalDateTime requested) {
        boolean hasConflict = reservationRepository.existsByTableIdAndReservationDateTimeBetween(
                tableId,
                requested.minusHours(2),    // início da janela (ex: reserva dura 2h)
                requested.plusHours(2)      // fim da janela
        );

        GlobalUtils.check(hasConflict,
                "A mesa já possui uma reserva neste horário.");
    }



    // =======================================================
    // 7. HORÁRIOS DO RESTAURANTE
    // =======================================================
    public Period validateRestaurantHours(LocalDateTime dateTime) {

        int day = dateTime.getDayOfWeek().getValue();   // 1 = segunda, 7 = domingo
        int hour = dateTime.getHour();

        boolean almoco = hour >= 11 && hour < 15;
        boolean jantar = hour >= 19 && hour < 23;

        // =============================
        // SEGUNDA-FEIRA → FECHADO
        // =============================
        GlobalUtils.check(
                day == 1,
                "O restaurante não funciona às segundas-feiras."
        );

        // =============================
        // DOMINGO → SOMENTE ALMOÇO
        // =============================
        if (day == 7) {
            GlobalUtils.check(
                    !almoco,
                    "Aos domingos funcionamos apenas no almoço (11h às 15h)."
            );
            return Period.LUNCH;
        }

        // =============================
        // TERÇA A SÁBADO
        // =============================
        GlobalUtils.check(
                !almoco && !jantar,
                "Horário inválido. Funcionamos 11h–15h (almoço) e 19h–23h (jantar) de terça a sábado."
        );

        return almoco ? Period.LUNCH : Period.DINNER;
    }




    // =======================================================
    // 8. LIMITE DE UMA RESERVA POR PERÍODO NO DIA
    // =======================================================
    public void validateUserPeriodLimit(Long userId, LocalDateTime dateTime) {

        Period period = validateRestaurantHours(dateTime);
        LocalDate day = dateTime.toLocalDate();

        LocalDateTime start;
        LocalDateTime end;

        if (period == Period.LUNCH) {
            start = day.atTime(11, 0);
            end   = day.atTime(15, 0);
        } else {
            start = day.atTime(19, 0);
            end   = day.atTime(23, 0);
        }

        List<StatusReservation> active = List.of(
                StatusReservation.PENDING,
                StatusReservation.CONFIRMED
        );

        boolean alreadyExists =
                !reservationRepository
                        .findByUserIdAndReservationDateTimeBetweenAndStatusIn(
                                userId,
                                start,
                                end,
                                active
                        )
                        .isEmpty();

        GlobalUtils.check(
                alreadyExists,
                "Você já possui uma reserva para este período."
        );
    }



    // =======================================================
    // 9. NO-SHOW
    // =======================================================
    public void validateNoShowLimit(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        // Se o usuário estiver com papel de bloqueado, verificar se o bloqueio ainda está ativo.
        // Se o bloqueio estiver ativo (blockedUntil após hoje) -> lançar erro.
        // Caso contrário (bloqueio expirado), restaurar o usuário para CUSTOMER e zerar contadores.
        if (user.getRole() == UserRole.BLOCK) {

            GlobalUtils.check(
                    user.getBlockedUntil() != null &&
                            user.getBlockedUntil().isAfter(LocalDate.now()),
                    "Você está bloqueado até " + user.getBlockedUntil() +
                            " devido a faltas consecutivas."
            );

            user.setRole(UserRole.CUSTOMER);
            user.setBlockedUntil(null);
            user.setNoShowCount(0);
            userRepository.save(user);
        }
    }

    public void validateDateTimeRules(LocalDateTime dateTime) {
        validateFutureDate(dateTime);
        validateRestaurantHours(dateTime);
        validateBlackout(dateTime);
    }

}
