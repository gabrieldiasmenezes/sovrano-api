package br.com.fiap.reserva_Sovrano.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.model.Users;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        // --------------------------
        // USUÁRIOS
        // --------------------------
        Users admin = userRepository.findByEmail("admin@sovrano.com")
                .orElseGet(() -> userRepository.save(
                        Users.builder()
                                .name("Admin Sovrano")
                                .email("admin@sovrano.com")
                                .phone("11900000000")
                                .password(passwordEncoder.encode("admin123"))
                                .role(UserRole.ADMIN)
                                .noShowCount(0)
                                .blockedUntil(null)
                                .build()
                ));

        Users user = userRepository.findByEmail("gabriel@sovrano.com")
                .orElseGet(() -> userRepository.save(
                        Users.builder()
                                .name("Gabriel Dias")
                                .email("gabriel@sovrano.com")
                                .phone("11999999999")
                                .password(passwordEncoder.encode("dias123"))
                                .role(UserRole.CUSTOMER)
                                .noShowCount(0)
                                .blockedUntil(null)
                                .build()
                ));

        // --------------------------
        // MESAS
        // --------------------------
        if (tableRepository.count() == 0) {

            List<Tables> tables = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                tables.add(Tables.builder().capacity(2).available(true).build());
            }

            for (int i = 0; i < 8; i++) {
                tables.add(Tables.builder().capacity(4).available(true).build());
            }

            for (int i = 0; i < 2; i++) {
                tables.add(Tables.builder().capacity(6).available(true).build());
            }

            tableRepository.saveAll(tables);
        }

        // --------------------------
        // RESERVAS
        // --------------------------
        if (reservationRepository.count() == 0) {

            List<Tables> tables = tableRepository.findAll();

            Tables reservedTable1 = tables.get(0);
            Tables reservedTable2 = tables.get(1);

            reservedTable1.setAvailable(false);
            reservedTable2.setAvailable(false);

            tableRepository.save(reservedTable1);
            tableRepository.save(reservedTable2);

            Reservations r1 = Reservations.builder()
                    .reservationDateTime(LocalDateTime.now().plusDays(1))
                    .peopleCount(2)
                    .status(StatusReservation.CONFIRMED)
                    .userId(admin.getId())
                    .tableId(reservedTable1.getId())
                    .build();

            Reservations r2 = Reservations.builder()
                    .reservationDateTime(LocalDateTime.now().plusDays(2))
                    .peopleCount(2)
                    .status(StatusReservation.CONFIRMED)
                    .userId(user.getId())
                    .tableId(reservedTable2.getId())
                    .build();

            reservationRepository.save(r1);
            reservationRepository.save(r2);
        }

        System.out.println("✔ Banco seedado sem duplicações!");
    }
}
