package br.com.fiap.reserva_Sovrano.controller;

import br.com.fiap.reserva_Sovrano.service.TokenService;
import br.com.fiap.reserva_Sovrano.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 📌 SWAGGER
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/login")
@Tag(
        name = "Auth",
        description = "Endpoint de autenticação pública para geração de token JWT"
)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Operation(
            summary = "Realiza login do usuário",
            description = """
                    Endpoint **público**, não requer autenticação.
                    
                    Recebe email e senha do usuário e retorna um token JWT válido.
                    O token deve ser utilizado nas requisições protegidas.
                    """,
            security = {} // 🔓 remove exigência de Bearer Token no Swagger
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso. Token retornado."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (faltando email ou senha)"),
            @ApiResponse(responseCode = "401", description = "Credenciais incorretas")
    })
    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        // autenticação
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // gerar token
        String token = tokenService.createToken(
                userService.findByEmail(email).get()
        );

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
