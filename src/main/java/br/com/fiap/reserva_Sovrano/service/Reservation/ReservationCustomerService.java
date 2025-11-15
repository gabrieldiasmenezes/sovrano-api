package br.com.fiap.reserva_Sovrano.service.Reservation;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.controller.Reservation.ReservationCustomerController.ReservationStatusFilter;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.specifications.ReservationCustomerSpecifications;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import br.com.fiap.reserva_Sovrano.utils.ReservationValidate;

@Service
public class ReservationCustomerService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationUtils reservationUtils;

    @Autowired
    private ReservationValidate reservationValidate;


    // ===============================
    // LISTAR MINHAS RESERVAS
    // ===============================
    public Page<ReservationResponseDTO> getMyReservations(
            Authentication auth,
            ReservationStatusFilter filter,
            Pageable pageable
    ) {
        Long userId = reservationUtils.getUserId(auth);

        var specification = Specification
                .where(ReservationCustomerSpecifications.belongsToUser(userId));

        // Se o usuário especificar um status (ex.: CONFIRMED)
        if (filter.status() != null) {
            specification = specification.and(
                    ReservationCustomerSpecifications.hasStatus(filter.status())
            );
        } else {
            // comportamento padrão: NÃO mostrar CANCELLED
            specification = specification.and(
                    ReservationCustomerSpecifications.excludeStatus(StatusReservation.CANCELLED)
            );
        }

        return reservationRepository.findAll(specification, pageable).map(reservationUtils :: toDTO);
    }


    // ===============================
    // CRIAR MINHA RESERVA
    // ===============================
    public Reservations createMyReservation(Reservations reservation, Authentication auth) {

        Long userId = reservationUtils.getUserId(auth);
        reservation.setUserId(userId); // garante que sempre será do usuário logado

        LocalDateTime dateTime = reservation.getReservationDateTime();

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva precisa ser feita para uma data futura.");
        }

        reservationValidate.validateUserPeriodLimit(userId, dateTime);
        Tables table = reservationUtils.getTable(reservation.getTableId());

        if (reservation.getPeopleCount() > table.getCapacity()) {
            throw new IllegalStateException(
                    "A mesa selecionada suporta apenas " + table.getCapacity() +
                    " pessoas."
            );
        }

        reservationValidate.validateAvailableTablesForPeople(
                reservation.getPeopleCount(),
                dateTime
        );

        reservationValidate.validateTableAvailability(
                table.getId(),
                dateTime
        );

        // 5. validar limite de no-shows do usuário
        reservationValidate.validateNoShowLimit(reservation.getUserId());

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }


    // ===============================
    // CONFIRMAR MINHA RESERVA
    // ===============================
    public Reservations confirmMyReservation(Long id, Authentication auth) {
        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation = reservationUtils.getReservationOwnedByUser(id, userId);

        reservationValidate.validateTableAvailability(
                reservation.getTableId(),
                reservation.getReservationDateTime()
        );

        reservationUtils.setTableAvailability(reservation.getTableId(), false);

        reservation.setStatus(StatusReservation.CONFIRMED);

        return reservationRepository.save(reservation);
    }


    // ===============================
    // CANCELAR MINHA RESERVA
    // ===============================
    public void cancelMyReservation(Long id, Authentication auth) {
        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation = reservationUtils.getReservationOwnedByUser(id, userId);

        reservationUtils.setTableAvailability(reservation.getTableId(), true);

        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }


    // ===============================
    // ATUALIZAR MINHA RESERVA
    // ===============================
    public Reservations updateMyReservation(Long id, Reservations data, Authentication auth) {
        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation = reservationUtils.getReservationOwnedByUser(id, userId);

        // Permitir atualizar apenas data/hora/mesa/pessoas
        if (data.getReservationDateTime() != null) {

            LocalDateTime dateTime = data.getReservationDateTime();

            if (dateTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("A reserva precisa ser feita para uma data futura.");
            }

            reservationValidate.validateRestaurantHours(dateTime);
            reservationValidate.validateAvailableTablesForPeople(data.getPeopleCount(), dateTime);
            reservationValidate.validateTableAvailability(reservation.getTableId(), dateTime);

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
