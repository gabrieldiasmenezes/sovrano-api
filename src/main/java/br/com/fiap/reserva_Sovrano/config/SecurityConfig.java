package br.com.fiap.reserva_Sovrano.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import br.com.fiap.reserva_Sovrano.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthFilter authFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, AuthFilter authFilter) {
        this.userDetailsService = userDetailsService;
        this.authFilter = authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()

                // ------------------------------------
                // PUBLIC
                // ------------------------------------
                .requestMatchers("/login/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                // ------------------------------------
                // CUSTOMER ROUTES (usuário autenticado)
                // ------------------------------------
                // O usuário só acessa o próprio perfil
                .requestMatchers("/users/me/**").authenticated()
   
                // O usuário só acessa **suas** reservas
                .requestMatchers("/reservations/me/**").hasRole("CUSTOMER")

                .requestMatchers("/waitlist/me/**").hasRole("CUSTOMER")
                // ------------------------------------
                // ADMIN ROUTES
                // ------------------------------------

                // Admin pode bloquear/desbloquear usuários
                .requestMatchers("users/{id}/unblock").hasRole("ADMIN")

                // Admin pode ver todas as reservas
                .requestMatchers("/reservations/**").hasRole("ADMIN")

                // Admin controla mesas
                .requestMatchers("/tables/**").hasRole("ADMIN")

                // Admin controla blackout times
                .requestMatchers("/blackouts/**").hasRole("ADMIN")

                .requestMatchers("/waitlist/**").hasRole("ADMIN")

                // Admin pode acessar qualquer outro endpoint
                .anyRequest().hasRole("ADMIN")

            .and()
            .sessionManagement().disable();

        http.addFilterBefore(
            authFilter,
            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



