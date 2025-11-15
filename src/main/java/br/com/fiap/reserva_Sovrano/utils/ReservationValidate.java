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


@Component
public class ReservationValidate {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    public void validateTableAvailability(Long id,LocalDateTime dateTime){
        boolean isOccupied=reservationRepository
                    .existsByTableIdAndReservationDateTimeAndStatus(id, dateTime, StatusReservation.CONFIRMED);
        if(isOccupied){
            throw new IllegalStateException("A mesa já foi reservada para esse horário");
        }
    }

    public void validateAvailableTablesForPeople(Integer peopleCount, LocalDateTime dateTime){
        // Busca mesas que possuem capacidade >= número de pessoas
        List<Tables> compatibleTables =
                tableRepository.findByCapacityGreaterThanEqual(peopleCount);

        if (compatibleTables.isEmpty()) {
            throw new IllegalStateException(
                    "Não existe nenhuma mesa com capacidade para " + peopleCount + " pessoas."
            );
        }

        // Verifica se alguma mesa está livre no horário
        boolean hasAvailable = compatibleTables.stream().anyMatch(table ->
                !reservationRepository.existsByTableIdAndReservationDateTimeAndStatus(
                        table.getId(),
                        dateTime,
                        StatusReservation.CONFIRMED
                )
        );

        if (!hasAvailable) {
            throw new IllegalStateException(
                    "Desculpe, não temos disponibilidade de reserva para esse horário para " + peopleCount + " pessoas."
            );
        }
    }


  public Period validateRestaurantHours(LocalDateTime dateTime){

        int day = dateTime.getDayOfWeek().getValue(); // 1 = segunda ... 7 = domingo
        int hour = dateTime.getHour();

        boolean almoco = hour >= 11 && hour < 15;
        boolean jantar = hour >= 19 && hour < 23;

        if (day == 7) {
            if (!almoco) {
                throw new IllegalArgumentException(
                    "O restaurante só funciona no almoço aos domingos (11h às 15h)."
                );
            }
            return Period.LUNCH;
        }

        if (!almoco && !jantar) {
            throw new IllegalArgumentException(
                "Horário inválido. Funcionamos 11h–15h e 19h–23h."
            );
        }

        return almoco ? Period.LUNCH : Period.DINNER;
    }

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
                    userId, start, end, active)
                .isEmpty();

        if (alreadyExists) {
            throw new IllegalStateException(
                "Você já possui uma reserva para este período (" 
                + (period == Period.LUNCH ? "almoço" : "jantar") +
                ") neste dia."
            );
        }
    }

    public void validateNoShowLimit(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (user.getRole() != UserRole.BLOCK) {

            if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(LocalDate.now())) {
                throw new IllegalStateException(
                    "Você está bloqueado até " + user.getBlockedUntil() + 
                    " devido a faltas consecutivas."
                );
            }

            // se passou dos 30 dias, desbloqueamos automaticamente
            user.setRole(UserRole.CUSTOMER);
            user.setBlockedUntil(null);
            user.setNoShowCount(0);
            userRepository.save(user);
        }
    }

    
}
