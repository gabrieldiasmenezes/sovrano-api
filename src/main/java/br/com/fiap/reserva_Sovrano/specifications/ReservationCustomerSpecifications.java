package br.com.fiap.reserva_Sovrano.specifications;


import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import org.springframework.data.jpa.domain.Specification;

public class ReservationCustomerSpecifications {

    public static Specification<Reservations> belongsToUser(Long userId) {
        return (root, query, builder) ->
                builder.equal(root.get("userId"), userId);
    }

    public static Specification<Reservations> hasStatus(StatusReservation status) {
        return (root, query, builder) -> {
            if (status == null) return builder.conjunction();
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<Reservations> excludeStatus(StatusReservation status) {
        return (root, query, builder) -> {
            if (status == null) return builder.conjunction();
            return builder.notEqual(root.get("status"), status);
        };
    }
}
