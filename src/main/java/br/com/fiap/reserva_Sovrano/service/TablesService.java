package br.com.fiap.reserva_Sovrano.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.reserva_Sovrano.model.Tables;
import br.com.fiap.reserva_Sovrano.repository.TableRepository;
import br.com.fiap.reserva_Sovrano.utils.GlobalUtils;

@Service
public class TablesService {

    @Autowired
    private TableRepository tableRepository;

    public List<Tables> listAll(){
        return tableRepository.findAll();
    }

    public Tables findById(Long id){
        return GlobalUtils.getOrThrow(tableRepository.findById(id),"Table not found.");
    }

    public Tables create(Tables table){
        return tableRepository.save(table);
    }

    public Tables update(Long id, Tables updatedTable) {
        Tables table = findById(id);
        table.setCapacity(updatedTable.getCapacity());
        return tableRepository.save(table);
    }

    public void delete(Long id) {
        GlobalUtils.check(!tableRepository.existsById(id),
            "Table not found."
        );
        tableRepository.deleteById(id);
    }
    
}


