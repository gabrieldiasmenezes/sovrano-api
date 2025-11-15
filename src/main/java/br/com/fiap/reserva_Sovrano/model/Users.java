package br.com.fiap.reserva_Sovrano.model;

import java.time.LocalDate;

import br.com.fiap.reserva_Sovrano.components.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100, message = "Name must have at most 100 characters.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(max = 20, message = "Phone number must have at most 20 characters.")
    private String phone;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must have at least 6 characters.")
    private String password;

    @NotNull(message = "User role is required.")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "no_show_count")
    private int noShowCount;

    @Column(name = "blocked_until")
    private LocalDate blockedUntil;

}
