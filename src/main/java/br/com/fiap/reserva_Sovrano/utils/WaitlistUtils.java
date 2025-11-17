package br.com.fiap.reserva_Sovrano.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Waitlist;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;


@Component
public class WaitlistUtils {

    @Autowired
    private ReservationRepository reservationRepository;

    // ... seus outros métodos (check, toDTO, getReservation, etc.)

    /**
     * Determina o periodo (ALMOCO/JANTAR) atual a partir do horário.
     * Retorna Period.LUNCH ou Period.DINNER (use seu enum Period).
     */
    public Period getCurrentPeriod() {
        int hour = LocalTime.now().getHour();
        boolean lunch = hour >= 11 && hour < 15;
        boolean dinner = hour >= 19 && hour < 23;

        if (lunch) return Period.LUNCH;
        if (dinner) return Period.DINNER;

        // fallback: se estiver fora dos horários, devolve o próximo período baseado no clock
        if (hour < 15) return Period.LUNCH;
        return Period.DINNER;
    }

    /**
     * Cria uma reserva PENDING para um usuário que foi notificado pela waitlist
     * — esta é a "reserva temporária" que fica aguardando confirmação do usuário.
     * O método associa a reservation ao tableId disponível.
     *
     * Observação: o tempo efetivo da reserva (reservationDateTime) pode ser definido
     * conforme sua regra; aqui usamos NOW para indicar imediata.
     */
    public Reservations createPendingWaitlistReservation(Waitlist waitlistEntry, Long tableId) {
        Reservations r = Reservations.builder()
                .reservationDateTime(LocalDateTime.now()) // reserva imediata; ajuste se preferir usar waitlistEntry.date+period
                .peopleCount(waitlistEntry.getPeopleCount())
                .status(StatusReservation.PENDING)
                .userId(waitlistEntry.getUserId())
                .tableId(tableId)
                .build();

        return reservationRepository.save(r);
    }

}
