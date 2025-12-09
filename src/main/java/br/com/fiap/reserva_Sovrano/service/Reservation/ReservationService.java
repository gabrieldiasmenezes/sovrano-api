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

    @Autowired
    private br.com.fiap.reserva_Sovrano.service.NotificationService notificationService;
    @Autowired
    private br.com.fiap.reserva_Sovrano.repository.UserRepository userRepository;


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
        // Se a reserva estiver vinculada a um usuário, aplicar validações específicas de usuário
        if (reservation.getUserId() != null) {
            reservationValidate.validateUserPeriodLimit(reservation.getUserId(), dateTime);
            reservationValidate.validateNoShowLimit(reservation.getUserId());
        }

        Tables table = reservationUtils.getTable(reservation.getTableId());

        reservationValidate.validateTableCapacity(table, reservation.getPeopleCount());
        reservationValidate.validateAvailableTablesForPeople(reservation.getPeopleCount(), dateTime);
        reservationValidate.validateTableAvailability(table.getId(), dateTime);
        

        if (!reservation.isHasLegalPriority()) {
            reservation.setLegalPriorityReason(null);
        }

        reservation.setStatus(StatusReservation.PENDING);

        Reservations saved = reservationRepository.save(reservation);

        // Enviar email de confirmação da criação (se usuário existir)
        if (saved.getUserId() != null) {
            var user = userRepository.findById(saved.getUserId()).orElse(null);
            if (user != null) notificationService.sendReservationCreatedEmail(user, saved);
        }

        return saved;
    }


    // ============================================================
    // CONFIRMAR
    // ============================================================
    public Reservations confirm(Long id) {
        Reservations reservation = reservationUtils.getReservation(id);

        // When confirming, ignore the current reservation when checking conflicts.
        var start = reservation.getReservationDateTime().minusHours(2);
        var end = reservation.getReservationDateTime().plusHours(2);

        boolean conflict = reservationRepository.findByReservationDateTimeBetween(start, end).stream()
                .anyMatch(r -> !java.util.Objects.equals(r.getId(), id)
                        && java.util.Objects.equals(r.getTableId(), reservation.getTableId())
                        && r.getStatus() == StatusReservation.CONFIRMED);

        GlobalUtils.check(conflict, "A mesa já possui uma reserva neste horário.");

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

            // Use final values (if people/table not provided, fall back to current reservation values)
            Integer finalPeopleCount = data.getPeopleCount() != null ? data.getPeopleCount() : reservation.getPeopleCount();
            Long finalTableId = data.getTableId() != null ? data.getTableId() : reservation.getTableId();

            reservationValidate.validateUserPeriodLimit(reservation.getUserId(), newDateTime);
            reservationValidate.validateAvailableTablesForPeople(finalPeopleCount, newDateTime);
            reservationValidate.validateTableAvailability(finalTableId, newDateTime);

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
