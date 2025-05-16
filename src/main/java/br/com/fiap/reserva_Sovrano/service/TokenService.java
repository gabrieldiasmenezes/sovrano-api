package br.com.fiap.reserva_Sovrano.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import br.com.fiap.reserva_Sovrano.components.UserRole;
import br.com.fiap.reserva_Sovrano.controller.AuthController.Token;
import br.com.fiap.reserva_Sovrano.model.Account;
@Service
public class TokenService {
    Instant expiresAt=LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.ofHours(-3));

    Algorithm algorithm=Algorithm.HMAC256("secret");
    public Token createToken(Account account){
        var jwt=JWT.create()
        .withSubject(account.getId().toString())
        .withClaim("email",account.getEmail())
        .withClaim("role", account.getRole().toString())
        .withExpiresAt(expiresAt)
        .sign(algorithm);

        return new Token(jwt, account.getEmail());
    }
    public Account getUserFromToken(String token){
        var verifiedToken=JWT.require(algorithm).build().verify(token);
        return Account.builder()
        .id(Long.valueOf(verifiedToken.getSubject()))
        .email(verifiedToken.getClaim("email").toString())
        .role(UserRole.valueOf(verifiedToken.getClaim("role").asString()))
        .build();
    }
    
}
