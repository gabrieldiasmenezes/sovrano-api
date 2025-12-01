package br.com.fiap.reserva_Sovrano.service.Reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.utils.GlobalUtils;
import br.com.fiap.reserva_Sovrano.utils.ReservationUtils;
import br.com.fiap.reserva_Sovrano.utils.ReservationValidate;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationUtils reservationUtils;

    @Autowired
    private ReservationValidate reservationValidate;


    // ============================================================
    // LISTAGENS
    // ============================================================
    public List<Reservations> listAll() {
        return reservationRepository.findAll();
    }

    public List<Reservations> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservations> listByTable(Long tableId) {
        return reservationRepository.findByTableId(tableId);
    }


    // ============================================================
    // CRIAR RESERVA
    // ============================================================
    public Reservations create(Reservations reservation) {

        LocalDateTime dateTime = reservation.getReservationDateTime();

        // Validações centralizadas
        reservationValidate.validateDateTimeRules(dateTime);
        reservationValidate.validateUserPeriodLimit(reservation.getUserId(), dateTime);

        Tables table = reservationUtils.getTable(reservation.getTableId());

        reservationValidate.validateTableCapacity(table, reservation.getPeopleCount());
        reservationValidate.validateAvailableTablesForPeople(reservation.getPeopleCount(), dateTime);
        reservationValidate.validateTableAvailability(table.getId(), dateTime);
        reservationValidate.validateNoShowLimit(reservation.getUserId());

        if (!reservation.isHasLegalPriority()) {
            reservation.setLegalPriorityReason(null);
        }

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }


    // ============================================================
    // CONFIRMAR
    // ============================================================
    public Reservations confirm(Long id) {
        Reservations reservation = reservationUtils.getReservation(id);

        reservationValidate.validateTableAvailability(
            reservation.getTableId(),
            reservation.getReservationDateTime()
        );

        reservation.setStatus(StatusReservation.CONFIRMED);
        return reservationRepository.save(reservation);
    }


    // ============================================================
    // CANCELAR
    // ============================================================
    public void cancel(Long id) {
        Reservations reservation = reservationUtils.getReservation(id);


        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }


    // ============================================================
    // FINALIZAR
    // ============================================================
    public Reservations completeReservation(Long id) {
        Reservations reservation = reservationUtils.getReservation(id);

        GlobalUtils.check(
            reservation.getStatus() != StatusReservation.CONFIRMED,
            "Só é possível finalizar reservas confirmadas."
        );

        reservation.setStatus(StatusReservation.COMPLETED);
        return reservationRepository.save(reservation);
    }


    // ============================================================
    // ATUALIZAR
    // ============================================================
    public Reservations update(Long id, Reservations data) {

        Reservations reservation = reservationUtils.getReservation(id);

        if (data.getReservationDateTime() != null) {

            LocalDateTime newDateTime = data.getReservationDateTime();

            reservationValidate.validateDateTimeRules(newDateTime);
            reservationValidate.validateUserPeriodLimit(reservation.getUserId(), newDateTime);
            reservationValidate.validateAvailableTablesForPeople(data.getPeopleCount(), newDateTime);
            reservationValidate.validateTableAvailability(data.getTableId(), newDateTime);

            reservation.setReservationDateTime(newDateTime);
        }

        if (data.getPeopleCount() != null) {
            reservation.setPeopleCount(data.getPeopleCount());
        }

        if (data.getTableId() != null) {
            reservation.setTableId(data.getTableId());
        }

        return reservationRepository.save(reservation);
    }


    // ============================================================
    // DELETAR
    // ============================================================
    public void delete(Long id) {
        GlobalUtils.check(
            !reservationRepository.existsById(id),
            "Reserva não encontrada."
        );

        reservationRepository.deleteById(id);
    }
}
