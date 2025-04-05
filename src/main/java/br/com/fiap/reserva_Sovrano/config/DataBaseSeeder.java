package br.com.fiap.reserva_Sovrano.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;

@Component
public class DataBaseSeeder {
    @Autowired
    private ReservationRepository reservationRepository;

    @PostConstruct
    public void init() {
        reservationRepository.saveAll(
                List.of(
                    Reservation.builder()
                    .name("Gabriel Dias Menezes")
                    .date(LocalDate.of(2025, 4, 10))
                    .time(LocalTime.of(19, 30))
                    .qnt(8)
                    .build(),

                    Reservation.builder().
                    name("Laura Menezes")
                    .date(LocalDate.of(2025, 4, 12))
                    .time(LocalTime.of(20, 0))
                    .qnt(2)
                    .status(StatusReserva.CONFIRMADA)
                    .build(),

                    Reservation.builder()
                    .name("Carlos Andrade")
                    .date(LocalDate.of(2025, 4, 15))
                    .time(LocalTime.of(12, 15))
                    .qnt(6)
                    .status(StatusReserva.CONFIRMADA)
                    .build()
                ));

    }
}
