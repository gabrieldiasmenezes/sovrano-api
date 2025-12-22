package br.com.fiap.reserva_Sovrano.specifications;

import br.com.fiap.reserva_Sovrano.controller.Reservation.ReservationController.ReservationFilter;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationSpecifications {

    public static Specification<Reservations> withFilters(ReservationFilter filter) {
        return (root, query, cb) -> {
            // If filter is null, return a conjunction (no filtering)
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            // FILTRAR por usuário
            if (filter.userId() != null) {
                predicates.add(cb.equal(root.get("userId"), filter.userId()));
            }

            // FILTRAR por data específica
            if (filter.date() != null) {
                LocalDateTime start = filter.date().atStartOfDay();
                LocalDateTime end = filter.date().atTime(23, 59, 59);
                predicates.add(cb.between(root.get("reservationDateTime"), start, end));
            }

            // FILTRAR por intervalo de horário
            if (filter.startTime() != null && filter.date() != null) {
                LocalDateTime start = LocalDateTime.of(filter.date(), filter.startTime());
                predicates.add(cb.greaterThanOrEqualTo(root.get("reservationDateTime"), start));
            }

            if (filter.endTime() != null && filter.date() != null) {
                LocalDateTime end = LocalDateTime.of(filter.date(), filter.endTime());
                predicates.add(cb.lessThanOrEqualTo(root.get("reservationDateTime"), end));
            }

            // FILTRAR por quantidade de pessoas
            if (filter.people() != null) {
                predicates.add(cb.equal(root.get("peopleCount"), filter.people()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
