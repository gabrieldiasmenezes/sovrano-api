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


    // =======================================================
    // LISTAR MINHAS RESERVAS
    // =======================================================
    public Page<ReservationResponseDTO> getMyReservations(
            Authentication auth,
            ReservationStatusFilter filter,
            Pageable pageable
    ) {
        Long userId = reservationUtils.getUserId(auth);

        var specification = Specification
                .where(ReservationCustomerSpecifications.belongsToUser(userId));

        if (filter.status() != null) {
            specification = specification.and(
                    ReservationCustomerSpecifications.hasStatus(filter.status())
            );
        } else {
            specification = specification.and(
                    ReservationCustomerSpecifications.excludeStatus(StatusReservation.CANCELLED)
            );
        }

        return reservationRepository.findAll(specification, pageable)
                .map(reservationUtils::toDTO);
    }



    // =======================================================
    // CRIAR MINHA RESERVA
    // =======================================================
    public Reservations createMyReservation(Reservations reservation, Authentication auth) {

        Long userId = reservationUtils.getUserId(auth);
        reservation.setUserId(userId);

        LocalDateTime dateTime = reservation.getReservationDateTime();

        // 🔹 Validações globais (data futura, blackout, horários, capacidade, mesa vaga...)
        reservationValidate.validateCreateOrUpdate(
                reservation.getPeopleCount(),
                reservation.getTableId(),
                dateTime
        );

        // 🔹 Limite de uma reserva por período no dia
        reservationValidate.validateUserPeriodLimit(userId, dateTime);

        // 🔹 Verificação de no-show
        reservationValidate.validateNoShowLimit(userId);

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }



    // =======================================================
    // CONFIRMAR MINHA RESERVA
    // =======================================================
    public Reservations confirmMyReservation(Long id, Authentication auth) {

        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation =
                reservationUtils.getReservationOwnedByUser(id, userId);

        // Verifica se a mesa ainda está livre para esse horário
        reservationValidate.validateTableAvailability(
                reservation.getTableId(),
                reservation.getReservationDateTime()
        );

        reservation.setStatus(StatusReservation.CONFIRMED);

        return reservationRepository.save(reservation);
    }



    // =======================================================
    // CANCELAR MINHA RESERVA
    // =======================================================
    public void cancelMyReservation(Long id, Authentication auth) {
        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation =reservationUtils.getReservationOwnedByUser(id, userId);

        reservation.setStatus(StatusReservation.CANCELLED);

        reservationRepository.save(reservation);
    }



    // =======================================================
    // ATUALIZAR MINHA RESERVA
    // =======================================================
    public Reservations updateMyReservation(Long id, Reservations data, Authentication auth) {

        Long userId = reservationUtils.getUserId(auth);

        Reservations reservation =
                reservationUtils.getReservationOwnedByUser(id, userId);

        LocalDateTime newDateTime = data.getReservationDateTime();
        Integer newPeopleCount = data.getPeopleCount();
        Long newTableId = data.getTableId();

        // 🔹 Atualização de data/hora/mesa/pessoas → validar tudo
        if (newDateTime != null || newPeopleCount != null || newTableId != null) {

            LocalDateTime finalDateTime =
                    newDateTime != null ? newDateTime : reservation.getReservationDateTime();

            Integer finalPeopleCount =
                    newPeopleCount != null ? newPeopleCount : reservation.getPeopleCount();

            Long finalTableId =
                    newTableId != null ? newTableId : reservation.getTableId();

            // Valida tudo de uma vez
            reservationValidate.validateCreateOrUpdate(
                    finalPeopleCount,
                    finalTableId,
                    finalDateTime
            );

            // Limite de período
            reservationValidate.validateUserPeriodLimit(userId, finalDateTime);

            // Aplicar mudanças
            reservation.setReservationDateTime(finalDateTime);
            reservation.setPeopleCount(finalPeopleCount);
            reservation.setTableId(finalTableId);
        }

        return reservationRepository.save(reservation);
    }
}
