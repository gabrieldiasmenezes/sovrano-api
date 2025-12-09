package br.com.fiap.reserva_Sovrano.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.*;
import br.com.fiap.reserva_Sovrano.model.*;
import br.com.fiap.reserva_Sovrano.repository.*;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseSeeder {

        private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired private UserRepository userRepository;
    @Autowired private TableRepository tableRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private WaitlistRepository waitlistRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @PostConstruct
    public void init() {

        // ============================================
        // USUÁRIOS (SEM PRIORIDADE — APENAS ROLE)
        // ============================================
        Users admin = createUserIfNotExists(
                "Admin Sovrano", "admin@sovrano.com", "11900000000",
                "admin123", UserRole.ADMIN
        );

        Users gabriel = createUserIfNotExists(
                "Gabriel Dias", "gabriel@sovrano.com", "11999999999",
                "dias123", UserRole.CUSTOMER
        );

        Users vip3 = createUserIfNotExists(
                "Cliente VIP 3", "vip3@sovrano.com", "11966665555",
                "teste123", UserRole.CUSTOMER
        );

        Users vip1 = createUserIfNotExists(
                "Cliente VIP 1", "vip1@sovrano.com", "11944443333",
                "teste123", UserRole.CUSTOMER
        );

        Users comum = createUserIfNotExists(
                "Cliente Comum", "comum@sovrano.com", "11922221111",
                "teste123", UserRole.CUSTOMER
        );


        // ============================================
        // MESAS
        // ============================================
        if (tableRepository.count() == 0) {

            List<Tables> tables = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                tables.add(Tables.builder()
                        .capacity(2)
                        .posX(random.nextInt(20) + 1)
                        .posY(random.nextInt(12) + 1)
                        .build());
            }

            for (int i = 0; i < 8; i++) {
                tables.add(Tables.builder()
                        .capacity(4)
                        .posX(random.nextInt(20) + 1)
                        .posY(random.nextInt(12) + 1)
                        .build());
            }

            for (int i = 0; i < 2; i++) {
                tables.add(Tables.builder()
                        .capacity(6)
                        .posX(random.nextInt(20) + 1)
                        .posY(random.nextInt(12) + 1)
                        .build());
            }

            tableRepository.saveAll(tables);
        }


        // ============================================
        // RESERVAS DE TESTE — COM PRIORIDADE
        // ============================================
        if (reservationRepository.count() == 0) {

            LocalDateTime LUNCHInicio = LocalDateTime.now().plusDays(1)
                    .withHour(12).withMinute(0);

            List<Tables> mesas = tableRepository.findAll();

            for (int i = 0; i < 10 && i < mesas.size(); i++) {

                Tables t = mesas.get(i);

                Reservations r = Reservations.builder()
                        .reservationDateTime(LUNCHInicio.plusMinutes(i * 10))
                        .peopleCount(t.getCapacity())
                        .status(StatusReservation.CONFIRMED)
                        .userId(comum.getId())
                        .tableId(t.getId())
                        .hasLegalPriority(false)
                        .legalPriorityReason(null)
                        .build();

                reservationRepository.save(r);
            }
        }


        // ============================================
        // WAITLIST DE TESTE (COM PRIORIDADE)
        // ============================================
        if (waitlistRepository.count() == 0) {

            LocalDate date = LocalDate.now().plusDays(1);

            List<Waitlist> list = List.of(

                    // VIP 3 (prioridade customizada)
                    Waitlist.builder()
                            .userId(vip3.getId())
                            .peopleCount(4)
                            .date(date)
                            .period(Period.LUNCH)
                            .status(WaitlistStatus.WAITING)
                            .hasLegalPriority(false)
                            .legalPriorityReason(null)
                            .createdAt(LocalDateTime.now().minusMinutes(10))
                            .build(),

                    // VIP 1
                    Waitlist.builder()
                            .userId(vip1.getId())
                            .peopleCount(2)
                            .date(date)
                            .period(Period.LUNCH)
                            .status(WaitlistStatus.WAITING)
                            .hasLegalPriority(false)
                            .legalPriorityReason(null)
                            .createdAt(LocalDateTime.now().minusMinutes(5))
                            .build(),

                    // Gabriel — comum
                    Waitlist.builder()
                            .userId(gabriel.getId())
                            .peopleCount(2)
                            .date(date)
                            .period(Period.LUNCH)
                            .status(WaitlistStatus.WAITING)
                            .hasLegalPriority(false)
                            .legalPriorityReason(null)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            waitlistRepository.saveAll(list);
        }

                logger.info("✔ Database SEED finalizado com o novo modelo (legalPriority).");
    }


    private Users createUserIfNotExists(
            String name, String email, String phone,
            String password, UserRole role
    ) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(
                        Users.builder()
                                .name(name)
                                .email(email)
                                .phone(phone)
                                .password(passwordEncoder.encode(password))
                                .role(role)
                                .visitsCount(0)
                                .noShowCount(0)
                                .blockedUntil(null)
                                .build()
                ));
    }
}
