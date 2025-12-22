package br.com.fiap.reserva_Sovrano.repository;

import br.com.fiap.reserva_Sovrano.model.BlackoutTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BlackoutTimeRepository extends JpaRepository<BlackoutTime, Long> {

    List<BlackoutTime> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime start,
            LocalDateTime end);
}
