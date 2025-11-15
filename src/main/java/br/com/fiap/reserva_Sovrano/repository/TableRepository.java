package br.com.fiap.reserva_Sovrano.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.reserva_Sovrano.model.Tables;

@Repository
public interface TableRepository extends JpaRepository<Tables, Long> {

    List<Tables> findByCapacityGreaterThanEqual(Integer capacity);
}
