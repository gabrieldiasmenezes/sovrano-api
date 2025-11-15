package br.com.fiap.reserva_Sovrano.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.model.dto.ReservationResponseDTO;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;
import br.com.fiap.reserva_Sovrano.repository.UserRepository;


@Component
public class ReservationUtils {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    public ReservationResponseDTO toDTO(Reservations r) {
        var user = userRepository.findById(r.getUserId())
                .orElse(null);

        return new ReservationResponseDTO(
                r.getId(),
                r.getReservationDateTime(),
                r.getPeopleCount(),
                r.getStatus(),
                user != null ? user.getEmail() : null,
                r.getTableId()
        )
        ;
    }

    public Reservations getReservation(Long id){
        return  reservationRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Reserva não encontrada."));
    }

    public Tables getTable(Long id){
        return  tableRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Mesa não encontrada."));
        
    }

    public void setTableAvailability(Long id, boolean available){
        Tables table=getTable(id);
        table.setAvailable(available);
        tableRepository.save(table);
    }


    public Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
    }

    public Reservations getReservationOwnedByUser(Long id, Long userId) {
        Reservations r = getReservation(id);

        if (!r.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Você não pode alterar uma reserva de outro usuário");
        }
        return r;
    }
    
}
