package br.com.fiap.reserva_Sovrano.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.reserva_Sovrano.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
}
