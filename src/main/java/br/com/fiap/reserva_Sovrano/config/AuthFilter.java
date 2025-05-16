package br.com.fiap.reserva_Sovrano.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.fiap.reserva_Sovrano.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter{
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {        
        //Verificar o header
        var header=request.getHeader("Authorization");
        System.out.println(header);
        if(header==null){
            filterChain.doFilter(request, response);
            return;
        }

        //tipo Bearer
        if(!header.startsWith("Bearer ")){
            response.setStatus(401);
            return;
        }

        //Validar Token
        var token=header.replace("Bearer ", "");
        var user=tokenService.getUserFromToken(token);
        System.out.println(user);
        var authentication=new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request,response);
    }

    
}

