package br.com.fiap.reserva_Sovrano.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;

@Component
public class DataBaseSeeder {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Random random = new Random();

    @PostConstruct
    public void init() {
        // Criar usuários
        var users = List.of(
            Account.builder().name("Gabriel Dias").email("gabriel@dias.com").password("123456").phone("(11) 91234-5678").build(),
            Account.builder().name("Laura Silva").email("laura@silva.com").password("abcdef").phone("(21) 99876-5432").build(),
            Account.builder().name("Carlos Souza").email("carlos@souza.com").password("senha123").phone("(31) 92345-6789").build()
        );

        accountRepository.saveAll(users);


        // Criar 50 reservas
        List<Reservation> reservations = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            var date = LocalDate.now().plusDays(random.nextInt(30)); // próximo mês
            var isAlmoco = random.nextBoolean();

            // Gera hora aleatória no intervalo certo
            var time = isAlmoco
                ? LocalTime.of(12 + random.nextInt(4), random.nextBoolean() ? 0 : 30) // 12:00 – 15:00
                : LocalTime.of(19 + random.nextInt(5), random.nextBoolean() ? 0 : 30); // 19:00 – 23:00

            var qnt = 2 + random.nextInt(7); // de 2 a 8 pessoas

            var status = random.nextBoolean() ? StatusReserva.CONFIRMADA : StatusReserva.PENDENTE;

            reservations.add(
                Reservation.builder()
                    .date(date)
                    .time(time)
                    .qnt(qnt)
                    .status(status)
                    .account(users.get(random.nextInt(users.size())))
                    .build()
            );
        }

        reservationRepository.saveAll(reservations);
    }
}
