package br.com.fiap.reserva_Sovrano.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;

@Component
public class ReservationUtils {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;


    public ReservationResponseDTO toDTO(Reservations r) {
        var user = userRepository.findById(r.getUserId()).orElse(null);

        return new ReservationResponseDTO(
                r.getId(),
                r.getReservationDateTime(),
                r.getPeopleCount(),
                r.getStatus(),
                user != null ? user.getEmail() : null,
                r.getTableId()
        );
    }

    // -------------------------
    // RESERVAS
    // -------------------------
    public Reservations getReservation(Long id) {
        return GlobalUtils.getOrThrow(
                reservationRepository.findById(id),
                "Reserva não encontrada."
        );
    }

    // -------------------------
    // MESAS
    // -------------------------
    public Tables getTable(Long id) {
        return GlobalUtils.getOrThrow(
                tableRepository.findById(id),
                "Mesa não encontrada."
        );
    }


    // -------------------------
    // USUÁRIOS
    // -------------------------
    public Long getUserId(Authentication auth) {
        return GlobalUtils.getOrThrow(
                userRepository.findByEmail(auth.getName()),
                "Usuário não encontrado."
        ).getId();
    }

    // -------------------------
    // RESERVA PERTENCE AO USUÁRIO
    // -------------------------
    public Reservations getReservationOwnedByUser(Long id, Long userId) {
        Reservations r = getReservation(id);

        GlobalUtils.check(
            !r.getUserId().equals(userId),
            "Você não pode alterar uma reserva de outro usuário"
        );

        return r;
    }

}
