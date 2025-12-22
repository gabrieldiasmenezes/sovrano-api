package br.com.fiap.reserva_Sovrano.components;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public enum UserRole {
    CUSTOMER,
    ADMIN,
    BLOCK;    

    // Converte o enum em GrantedAuthority para o Spring Security
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.name()));
    }
}
