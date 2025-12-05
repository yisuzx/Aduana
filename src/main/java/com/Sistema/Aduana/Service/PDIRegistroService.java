package com.Sistema.Aduana.Service;

import com.Sistema.Aduana.Entity.PDIRegistro;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Repository.PDIRegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDIRegistroService {
    private final PDIRegistroRepository registroRepository;

    public PDIRegistro save(PDIRegistro registro) {
        return registroRepository.save(registro);
    }

    public PDIRegistro crearRegistro(User agente, PDIRegistro registro) {
        registro.setAgente(agente);
        registro.setFechaRegistro(java.time.LocalDateTime.now());
        return save(registro);
    }

    public List<PDIRegistro> findByAgente(User agente) {
        return registroRepository.findByAgenteId(agente.getId());
    }

    public List<PDIRegistro> findAll() {
        return registroRepository.findAll();
    }

    public long contarIngresosHoy() {
        return registroRepository.countByFechaIngreso(LocalDate.now());
    }
}