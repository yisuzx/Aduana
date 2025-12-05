package com.Sistema.Aduana.Repository;

import com.Sistema.Aduana.Entity.Inspeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InspeccionRepository extends JpaRepository<Inspeccion, Long> {
    long countByResultado(String resultado);
    List<Inspeccion> findByInspectorId(Long inspectorId);
}