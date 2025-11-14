package br.com.fiap.reserva_Sovrano.repository;


import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservations, Long> {

    // Buscar reservas por usuário
    List<Reservations> findByUserId(Long userId);

    List<Reservations> findByTableId(Long tableId);

    // Verificar se mesa está ocupada em um horário
    boolean existsByTableIdAndReservationDateTimeAndStatus(
        Long tableId,
        LocalDateTime reservationDateTime,
        StatusReservation status
    );
}
