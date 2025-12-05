package com.Sistema.Aduana.Repository;

import com.Sistema.Aduana.Entity.PDIRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PDIRegistroRepository extends JpaRepository<PDIRegistro, Long> {
    List<PDIRegistro> findByAgenteId(Long agenteId);
    List<PDIRegistro> findByFechaIngreso(LocalDate fecha);
    List<PDIRegistro> findByEstado(String estado);
    long countByFechaIngreso(LocalDate fecha);
}