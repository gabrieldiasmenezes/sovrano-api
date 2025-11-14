package br.com.fiap.reserva_Sovrano.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;


    private Reservations getReservation(Long id){
        return  reservationRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Reserva não encontrada."));
    }

    private Tables getTable(Long id){
        return  tableRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Mesa não encontrada."));
        
    }

    private void validateTableAvailability(Long id,LocalDateTime dateTime){
        boolean isOccupied=reservationRepository
                    .existsByTableIdAndReservationDateTimeAndStatus(id, dateTime, StatusReservation.CONFIRMED);
        if(isOccupied){
            throw new IllegalStateException("A mesa já foi reservada para esse horário");
        }
    }

    private void setTableAvailability(Long id, boolean available){
        Tables table=getTable(id);
        table.setAvailable(available);
        tableRepository.save(table);
    }



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
        if(reservation.getReservationDateTime().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva só poderá ser feita em uma data futura");
        }

       validateTableAvailability( reservation.getTableId(), reservation.getReservationDateTime());

        reservation.setStatus(StatusReservation.PENDING);

        return reservationRepository.save(reservation);
    }

    public Reservations confirm(Long id) {
        Reservations reservation=getReservation(id);
        
        validateTableAvailability(reservation.getTableId(), reservation.getReservationDateTime());
        

        setTableAvailability(reservation.getTableId(), false);
  
        reservation.setStatus(StatusReservation.CONFIRMED);
        return reservationRepository.save(reservation);
    }

    public void cancel(Long id) {
        Reservations reservation=getReservation(id);
        
        setTableAvailability(reservation.getTableId(), true);

        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        if(!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Reservation not found.");
        }
        reservationRepository.deleteById(id);
    }

    
}

