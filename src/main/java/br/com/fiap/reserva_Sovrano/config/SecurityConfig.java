package br.com.fiap.reserva_Sovrano.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private AuthFilter authFilter;
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeHttpRequests(auth -> auth
            // Libera primeiro o que for público
                .requestMatchers("/login").permitAll()
                .requestMatchers("/login/**").permitAll()
                
                // Depois coloca as regras protegidas
                .requestMatchers(HttpMethod.GET, "/reservations/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/reservations/user/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/reservations/**").hasAnyRole("USER")
                // Qualquer outra rota precisa estar autenticada
                .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.disable())
        .addFilterBefore(authFilter,UsernamePasswordAuthenticationFilter.class)
        .httpBasic(Customizer.withDefaults())
        .build();
    }
    // @Bean
    // UserDetailsService userDetailsService() {
    //     var user1=User.withUsername("gabriel")
    //     .password("$2a$12$cqqq/j0Mq/Fk36QKAq3.ke36yuhwkmNrJLVBwhSJGMu0TkQCl9Q4K")
    //     .roles("ADMIN")
    //     .build();
    //     var user2=User.withUsername("maria")
    //     .password("$2a$12$x0Az8q6EHega0Na61JFhouuEeX62or7n0RgSv14iZtNTY6Xa7hPV2")
    //     .roles("USER")
    //     .build();
    //     var users=List.of(user1,user2);
    //     return new InMemoryUserDetailsManager(users);
    // }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
