package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDateTime;
import java.util.Random;

import br.com.fiap.reserva_Sovrano.components.StatusReserva;



public class Reservation {
    
    private Long idReserva;
    private LocalDateTime reservationDate;
    private int quantPessoas;
    private StatusReserva status;
    public Reservation(Long idReserva, User user, LocalDateTime reservationDate, int quantPessoas,
            StatusReserva status) {
        this.idReserva = Math.abs(new Random().nextLong());
        this.reservationDate = reservationDate;
        this.quantPessoas = quantPessoas;
        this.status = status;
    }
    public Long getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }
    public int getQuantPessoas() {
        return quantPessoas;
    }
    public void setQuantPessoas(int quantPessoas) {
        this.quantPessoas = quantPessoas;
    }
    public StatusReserva getStatus() {
        return status;
    }
    public void setStatus(StatusReserva status) {
        this.status = status;
    }
    
    
    
}
