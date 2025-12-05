package com.Sistema.Aduana.Repository;

import com.Sistema.Aduana.Entity.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByRolUsuario(String rolUsuario);
    List<Reporte> findByRolUsuarioAndFechaGeneracionBetween(String rolUsuario, LocalDateTime inicio, LocalDateTime fin);
}