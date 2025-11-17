package br.com.fiap.reserva_Sovrano.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.WaitlistStatus;
import br.com.fiap.reserva_Sovrano.model.Waitlist;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    List<Waitlist> findByDateAndPeriodAndStatusOrderByCreatedAt(
            LocalDate date,
            Period period,
            WaitlistStatus status
    );

    List<Waitlist> findByUserIdAndStatusOrderByCreatedAt(Long userId, WaitlistStatus status);

    Optional<Waitlist> findByUserIdAndDateAndPeriod(Long userId, LocalDate date, Period period);
}