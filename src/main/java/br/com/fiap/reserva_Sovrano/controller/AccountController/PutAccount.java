package br.com.fiap.reserva_Sovrano.controller.AccountController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.reserva_Sovrano.model.Account;
import br.com.fiap.reserva_Sovrano.repository.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/accounts")
public class PutAccount {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository repository;

    @PutMapping("{id}")
    @Operation(
        tags = "Usuários",
        summary = "Atualizar dados do usuário",
        description = "Atualiza os dados de um usuário existente com as informações fornecidas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
        }
    )
    public Account update(@PathVariable Long id, @RequestBody Account account) {
        log.info("Atualizando dados do usuário ID: " + id);
        account.setId(id);
        repository.save(account);
        return account;
    }
    
}
