package br.com.fiap.reserva_Sovrano.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.fiap.reserva_Sovrano.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,JpaSpecificationExecutor<Reservation> {
    
    List<Reservation> findByAccount_Email(String email);

}
