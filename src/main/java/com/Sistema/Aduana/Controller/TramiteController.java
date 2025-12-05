package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.Tramite;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Repository.TramiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final TramiteRepository tramiteRepository;

    @GetMapping
    public ResponseEntity<?> getTramites() {
        return ResponseEntity.ok(tramiteRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crearTramite(@RequestBody Tramite tramite) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tramite.setUsuario(user);
        tramite.setNumero("TR-" + System.currentTimeMillis());
        return ResponseEntity.ok(tramiteRepository.save(tramite));
    }

    @GetMapping("/estado-aduanero")
    @PreAuthorize("hasRole('ADUANA')")
    public ResponseEntity<?> getEstadoAduanero() {
        return ResponseEntity.ok(Map.of(
                "estado", "EN REVISIÓN FÍSICA",
                "aduana", "Los Libertadores",
                "fechaActualizacion", java.time.LocalDateTime.now().toString(),
                "observaciones", "Mercancía detenida para inspección aleatoria."
        ));
    }
}