package br.com.fiap.reserva_Sovrano.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;
import br.com.fiap.reserva_Sovrano.model.Reservation;
import br.com.fiap.reserva_Sovrano.repository.ReservationRepository;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final Logger log=LoggerFactory.getLogger(getClass());
    //Injecao de dependencia
    @Autowired
    private ReservationRepository repository;

    @GetMapping
    public List<Reservation> index(){
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(code=HttpStatus.CREATED)
    public ResponseEntity<Reservation> create(@RequestBody @Valid Reservation reservation) {
        log.info("Criando reserva {}", reservation);
        reservation.setStatus(StatusReserva.CONFIRMADA);
        repository.save(reservation);
        return ResponseEntity.status(201).body(reservation);
    }

    @GetMapping("{idReserva}")
    public Reservation get(@PathVariable Long idReserva) {
        log.info("buscando reserva "+idReserva);
        return getReservation(idReserva);
    }

    @DeleteMapping("{idReserva}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long idReserva){
        log.info("Cancelando reserva " + idReserva);
        Reservation reservation = getReservation(idReserva);
        reservation.setStatus(StatusReserva.CANCELADA);
        repository.save(reservation);
    }
    @DeleteMapping("excluir/{idReserva}")
    public void delete(@PathVariable Long idReserva){
        log.info("Cancelando reserva " + idReserva);
        Reservation reservation = getReservation(idReserva);
        repository.delete(reservation);
    }

    @PutMapping("{idReserva}")
    public Reservation update(@PathVariable Long idReserva,@RequestBody Reservation reservation){
        log.info("Atualizando as reservas "+idReserva);
        reservation.setIdReserva(idReserva);
        repository.save(reservation);
        return reservation;
    }
        
    private Reservation getReservation(Long idReserva){
        return repository.findById(idReserva).
        orElseThrow(
            ()->new ResponseStatusException
            (HttpStatus.NOT_FOUND)
        );
    }
      
}
