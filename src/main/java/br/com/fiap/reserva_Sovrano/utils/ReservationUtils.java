package br.com.fiap.reserva_Sovrano.utils;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.fiap.reserva_Sovrano.components.StatusReservation;
import br.com.fiap.reserva_Sovrano.model.Reservations;
import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;


@Component
public class ReservationUtils {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;


    public Reservations getReservation(Long id){
        return  reservationRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Reserva não encontrada."));
    }

    public Tables getTable(Long id){
        return  tableRepository.findById(id)
                    .orElseThrow(()-> new IllegalArgumentException("Mesa não encontrada."));
        
    }

    public void validateTableAvailability(Long id,LocalDateTime dateTime){
        boolean isOccupied=reservationRepository
                    .existsByTableIdAndReservationDateTimeAndStatus(id, dateTime, StatusReservation.CONFIRMED);
        if(isOccupied){
            throw new IllegalStateException("A mesa já foi reservada para esse horário");
        }
    }

    public void setTableAvailability(Long id, boolean available){
        Tables table=getTable(id);
        table.setAvailable(available);
        tableRepository.save(table);
    }

    public void validateAvailableTablesForPeople(Integer peopleCount, LocalDateTime dateTime){
        // Busca mesas que possuem capacidade >= número de pessoas
        List<Tables> compatibleTables =
                tableRepository.findByCapacityGreaterThanEqual(peopleCount);

        if (compatibleTables.isEmpty()) {
            throw new IllegalStateException(
                    "Não existe nenhuma mesa com capacidade para " + peopleCount + " pessoas."
            );
        }

        // Verifica se alguma mesa está livre no horário
        boolean hasAvailable = compatibleTables.stream().anyMatch(table ->
                !reservationRepository.existsByTableIdAndReservationDateTimeAndStatus(
                        table.getId(),
                        dateTime,
                        StatusReservation.CONFIRMED
                )
        );

        if (!hasAvailable) {
            throw new IllegalStateException(
                    "Desculpe, não temos disponibilidade de reserva para esse horário para " + peopleCount + " pessoas."
            );
        }
    }


    public void validateRestaurantHours(LocalDateTime dateTime){
        int day = dateTime.getDayOfWeek().getValue(); // 1 = segunda, ..., 7 = domingo
        int hour = dateTime.getHour();

        boolean almoco = hour >= 11 && hour < 15;
        boolean jantar = hour >= 19 && hour < 23;

        if (day == 7) { // domingo
            if (!almoco) {
                throw new IllegalArgumentException("O restaurante só funciona no almoço aos domingos (11h às 15h).");
            }
        } else {
            if (!almoco && !jantar) {
                throw new IllegalArgumentException("Horário inválido. Funcionamos 11h–15h e 19h–23h.");
            }
        }
    }
    
}
