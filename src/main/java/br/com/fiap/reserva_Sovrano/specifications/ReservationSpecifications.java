package br.com.fiap.reserva_Sovrano.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.fiap.reserva_Sovrano.controller.ReservationController.GetReservationList.ReservationFilter;
import br.com.fiap.reserva_Sovrano.model.Reservation;

import jakarta.persistence.criteria.Predicate;

public class ReservationSpecifications {

    public static Specification<Reservation> withFilters(ReservationFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.description() != null && !filter.description().isBlank()) {
                predicates.add(
                    cb.like(cb.lower(root.get("name")), "%" + filter.description().toLowerCase() + "%")
                );
            }

            if (filter.date() != null) {
                predicates.add(
                    cb.equal(root.get("date"), filter.date())
                );
            }

            if (filter.qnt() != null) {
                predicates.add(cb.equal(root.get("qnt"), filter.qnt()));
            }
            var arrayPredicates=predicates.toArray(new Predicate[0]);
            return cb.and(arrayPredicates);
        };
    }
}
