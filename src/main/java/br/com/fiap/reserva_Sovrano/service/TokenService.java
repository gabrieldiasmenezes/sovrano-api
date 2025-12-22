package br.com.fiap.reserva_Sovrano.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import br.com.fiap.reserva_Sovrano.model.Users;

@Service
public class TokenService {

    private final String jwtSecret = "S3cr3tJWTKey"; // Coloque em application.properties
    private final Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

    // Expiração do token: 1 dia
    private final Instant expiresAt = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.ofHours(-3));

    // Cria token JWT a partir do usuário
    public String createToken(Users user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("email", user.getEmail())
                .withClaim("role", user.getRole().toString())
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    // Recupera o email do usuário a partir do token JWT
    public String getUserFromToken(String token) {
        try {
            var verifiedToken = JWT.require(algorithm).build().verify(token);
            return verifiedToken.getClaim("email").asString(); // retorna email
        } catch (JWTVerificationException e) {
            return null; // token inválido
        }
    }
}
