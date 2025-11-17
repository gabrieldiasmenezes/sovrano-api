package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.fiap.reserva_Sovrano.components.Period;
import br.com.fiap.reserva_Sovrano.components.WaitlistStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waitlist")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer peopleCount;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Period period; // ALMOCO / JANTAR

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private WaitlistStatus status; // WAITING, NOTIFIED, EXPIRED
}
