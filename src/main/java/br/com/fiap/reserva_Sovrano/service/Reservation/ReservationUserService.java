package br.com.fiap.reserva_Sovrano.service.Reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;

@Service
public class ReservationUserService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationUtils reservationMethods;

    @Autowired
    private UserRepository userRepository;


    // ===============================
    // UTIL
    // ===============================

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
    }

    private Reservations getReservationOwnedByUser(Long id, Long userId) {
        Reservations r = reservationMethods.getReservation(id);

        if (!r.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Você não pode alterar uma reserva de outro usuário");
        }
        return r;
    }


    // ===============================
    // LISTAR MINHAS RESERVAS
    // ===============================
    public List<Reservations> getMyReservations(Authentication auth) {
        Long userId = getUserId(auth);
        return reservationRepository.findByUserIdAndStatusNot(userId, StatusReservation.CANCELLED);
    }


    // ===============================
    // CRIAR MINHA RESERVA
    // ===============================
    public Reservations createMyReservation(Reservations reservation, Authentication auth) {

        Long userId = getUserId(auth);
        reservation.setUserId(userId); // garante que sempre será do usuário logado

        LocalDateTime dateTime = reservation.getReservationDateTime();

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva precisa ser feita para uma data futura.");
        }

        reservationMethods.validateRestaurantHours(dateTime);

        Tables table = reservationMethods.getTable(reservation.getTableId());

        if (reservation.getPeopleCount() > table.getCapacity()) {
            throw new IllegalStateException(
                    "A mesa selecionada suporta apenas " + table.getCapacity() +
                    " pessoas."
            );
        }

        reservationMethods.validateAvailableTablesForPeople(
                reservation.getPeopleCount(),
                dateTime
        );

        reservationMethods.validateTableAvailability(
                table.getId(),
                dateTime
        );

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }


    // ===============================
    // CONFIRMAR MINHA RESERVA
    // ===============================
    public Reservations confirmMyReservation(Long id, Authentication auth) {
        Long userId = getUserId(auth);

        Reservations reservation = getReservationOwnedByUser(id, userId);

        reservationMethods.validateTableAvailability(
                reservation.getTableId(),
                reservation.getReservationDateTime()
        );

        reservationMethods.setTableAvailability(reservation.getTableId(), false);

        reservation.setStatus(StatusReservation.CONFIRMED);

        return reservationRepository.save(reservation);
    }


    // ===============================
    // CANCELAR MINHA RESERVA
    // ===============================
    public void cancelMyReservation(Long id, Authentication auth) {
        Long userId = getUserId(auth);

        Reservations reservation = getReservationOwnedByUser(id, userId);

        reservationMethods.setTableAvailability(reservation.getTableId(), true);

        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }


    // ===============================
    // ATUALIZAR MINHA RESERVA
    // ===============================
    public Reservations updateMyReservation(Long id, Reservations data, Authentication auth) {
        Long userId = getUserId(auth);

        Reservations reservation = getReservationOwnedByUser(id, userId);

        // Permitir atualizar apenas data/hora/mesa/pessoas
        if (data.getReservationDateTime() != null) {

            LocalDateTime dateTime = data.getReservationDateTime();

            if (dateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("A reserva precisa ser feita para uma data futura.");
            }

            reservationMethods.validateRestaurantHours(dateTime);
            reservationMethods.validateAvailableTablesForPeople(data.getPeopleCount(), dateTime);
            reservationMethods.validateTableAvailability(reservation.getTableId(), dateTime);

            reservation.setReservationDateTime(dateTime);
        }

        if (data.getPeopleCount() != null) {
            reservation.setPeopleCount(data.getPeopleCount());
        }

        if (data.getTableId() != null) {
            reservation.setTableId(data.getTableId());
        }

        return reservationRepository.save(reservation);
    }
}
