package br.com.fiap.reserva_Sovrano.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.model.BlackoutTime;
import br.com.fiap.reserva_Sovrano.model.dto.BlackoutTimeRequestDTO;
import br.com.fiap.reserva_Sovrano.repository.BlackoutTimeRepository;


@Service
public class BlackoutTimeService {

    @Autowired
    private  BlackoutTimeRepository repository;

    public BlackoutTime create(BlackoutTimeRequestDTO dto) {
        if (dto.getEndTime().isBefore(dto.getStartTime()))
            throw new IllegalArgumentException("End time must be after start time.");

        BlackoutTime blackout = BlackoutTime.builder()
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .reason(dto.getReason())
                .build();

        return repository.save(blackout);
    }

    public List<BlackoutTime> getAll() {
        return repository.findAll();
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean isInBlackout(LocalDateTime time) {
        return !repository
                .findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(time, time)
                .isEmpty();
    }
}
