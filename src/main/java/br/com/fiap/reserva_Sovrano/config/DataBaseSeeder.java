package br.com.fiap.reserva_Sovrano.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.components.UserRole;
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

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Random random = new Random();

    @PostConstruct
    public void init() {
        // Criar um admin (sem reservas)
        var admin = Account.builder()
            .name("Admin Restaurante")
            .email("admin@restaurante.com")
            .password(passwordEncoder.encode("admin123"))
            .phone("(11) 90000-0000")
            .role(UserRole.ADMIN)
            .build();

        // Criar 3 usuários comuns
        var user1 = Account.builder()
            .name("Gabriel Dias")
            .email("gabriel@dias.com")
            .password(passwordEncoder.encode("123456"))
            .phone("(11) 91234-5678")
            .role(UserRole.USER)
            .build();

        var user2 = Account.builder()
            .name("Laura Silva")
            .email("laura@silva.com")
            .password(passwordEncoder.encode("123456"))
            .phone("(21) 99876-5432")
            .role(UserRole.USER)
            .build();

        var user3 = Account.builder()
            .name("Carlos Souza")
            .email("carlos@souza.com")
            .password(passwordEncoder.encode("123456"))
            .phone("(31) 92345-6789")
            .role(UserRole.USER)
            .build();

        var allAccounts = List.of(admin, user1, user2, user3);
        accountRepository.saveAll(allAccounts);

        // Criar reservas para os usuários (NÃO para o admin)
        List<Account> usersOnly = List.of(user1, user2, user3);
        List<Reservation> reservations = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            var date = LocalDate.now().plusDays(random.nextInt(30));
            var isAlmoco = random.nextBoolean();

            var time = isAlmoco
                ? LocalTime.of(12 + random.nextInt(4), random.nextBoolean() ? 0 : 30)
                : LocalTime.of(19 + random.nextInt(5), random.nextBoolean() ? 0 : 30);

            var qnt = 2 + random.nextInt(7);

            var status = random.nextBoolean() ? StatusReserva.CONFIRMADA : StatusReserva.PENDENTE;

            reservations.add(
                Reservation.builder()
                    .date(date)
                    .time(time)
                    .qnt(qnt)
                    .status(status)
                    .account(usersOnly.get(random.nextInt(usersOnly.size())))
                    .build()
            );
        }

        reservationRepository.saveAll(reservations);
    }
}
