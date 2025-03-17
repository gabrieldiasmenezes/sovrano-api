package br.com.fiap.reserva_Sovrano.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.reserva_Sovrano.model.Account;
public interface AccountRepository extends JpaRepository<Account,Long>{
    
}
