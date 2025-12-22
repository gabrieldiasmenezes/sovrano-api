package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;

import br.com.fiap.reserva_Sovrano.components.PriorityType;
import br.com.fiap.reserva_Sovrano.components.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required.")
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Password is required.")
    @Size(min = 6)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    // ---------- PRIORIDADES E VIP ----------
    @Enumerated(EnumType.STRING)
    @Column(name = "vip_level")
    private PriorityType vipLevel;        // VIP_1, VIP_2...


    @Column(name = "visits_count")
    private int visitsCount;                   // visitas acumuladas (VIP automático)

    @Column(name = "photo_url")
    private String photoUrl;                   // opcional

    // ---------- CONTROLE DE PUNIÇÃO ----------
    @Column(name = "no_show_count")
    private int noShowCount;

    @Column(name = "blocked_until")
    private LocalDate blockedUntil;
}
