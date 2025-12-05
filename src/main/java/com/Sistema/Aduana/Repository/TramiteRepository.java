package com.Sistema.Aduana.Repository;

import com.Sistema.Aduana.Entity.Tramite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {
    List<Tramite> findByUsuarioId(Long usuarioId);
    List<Tramite> findByEstado(String estado);
    List<Tramite> findByTipo(String tipo);
}