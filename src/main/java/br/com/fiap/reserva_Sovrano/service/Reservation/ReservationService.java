package br.com.fiap.reserva_Sovrano.service.Reservation;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
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



    public List<Reservations> listAll() {
        return reservationRepository.findAll();
    }

    public List<Reservations> findByUser(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservations> listByTable(Long tableId) {
        return reservationRepository.findByTableId(tableId);
    }

    public Reservations create(Reservations reservation) {
        LocalDateTime dateTime = reservation.getReservationDateTime();

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva precisa ser feita para uma data futura.");
        }

        // 1. validar horário do restaurante
        reservationValidate.validateUserPeriodLimit(reservation.getUserId(), dateTime);

        // 2. validar capacidade da mesa escolhida
        Tables table = reservationUtils.getTable(reservation.getTableId());
        if (reservation.getPeopleCount() > table.getCapacity()) {
            throw new IllegalStateException(
                    "A mesa selecionada suporta apenas " + table.getCapacity() + 
                    " pessoas. Selecione outra mesa."
            );
        }

        // 3. validar se existe mesa compatível disponível no horário
        reservationValidate.validateAvailableTablesForPeople(
                reservation.getPeopleCount(),
                dateTime
        );

        // 4. validar se aquela mesa específica está livre
        reservationValidate.validateTableAvailability(table.getId(), dateTime);

        reservation.setStatus(StatusReservation.PENDING);
        return reservationRepository.save(reservation);
    }


    public Reservations confirm(Long id) {
        Reservations reservation=reservationUtils.getReservation(id);
        
        reservationValidate.validateTableAvailability(reservation.getTableId(), reservation.getReservationDateTime());
        

        reservationUtils.setTableAvailability(reservation.getTableId(), false);
  
        reservation.setStatus(StatusReservation.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public void cancel(Long id) {
        Reservations reservation=reservationUtils.getReservation(id);
        
        reservationUtils.setTableAvailability(reservation.getTableId(), true);

        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }

    public Reservations completeReservation(Long id) {
        Reservations res = reservationUtils.getReservation(id);

        if (res.getStatus() != StatusReservation.CONFIRMED) {
            throw new IllegalStateException("Só é possível finalizar reservas confirmadas.");
        }

        reservationUtils.setTableAvailability(res.getTableId(), true);
        res.setStatus(StatusReservation.COMPLETED);
        return reservationRepository.save(res);
    }

    public void delete(Long id) {
        if(!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        reservationRepository.deleteById(id);
    }

    
}

