package br.com.fiap.reserva_Sovrano.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.service.TokenService;

@RestController
public class AuthController {
    
    public record Token(String token,String email){}
    public record Credentials(String email, String password){}

    @Autowired
    private TokenService tokenService;

    @Autowired
    AuthenticationManager authManager;

    @PostMapping("/login")
    public Token login(@RequestBody Credentials credentials){
        var authentication=new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());
        var user=(Account) authManager.authenticate(authentication).getPrincipal();

        return tokenService.createToken(user);
    }

}
