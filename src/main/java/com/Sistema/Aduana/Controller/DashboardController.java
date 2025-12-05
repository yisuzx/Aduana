package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Repository.InspeccionRepository;
import com.Sistema.Aduana.Repository.PDIRegistroRepository;
import com.Sistema.Aduana.Repository.TramiteRepository;
import com.Sistema.Aduana.Repository.ReporteRepository; // Importar esto
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TramiteRepository tramiteRepository;
    private final PDIRegistroRepository pdiRepository;
    private final InspeccionRepository inspeccionRepository;
    private final ReporteRepository reporteRepository;


    @GetMapping("/usuario")
    @PreAuthorize("hasRole('ADUANA')")
    public ResponseEntity<?> getDashboardAduana() {
        User user = getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("user", getUserInfo(user));

        long total = tramiteRepository.count();
        long pendientes = tramiteRepository.findByEstado("PENDIENTE").size();
        long aprobados = tramiteRepository.findByEstado("APROBADO").size();

        data.put("stats", Map.of(
                "tramitesPendientes", pendientes,
                "tramitesAprobados", aprobados,
                "documentosPendientes", 0,
                "alertas", total - (pendientes + aprobados)
        ));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/pdi")
    @PreAuthorize("hasRole('PDI')")
    public ResponseEntity<?> getDashboardPDI() {
        User user = getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("user", getUserInfo(user));

        long totalRegistros = pdiRepository.count();
        long ingresosHoy = pdiRepository.countByFechaIngreso(LocalDate.now());

        data.put("stats", Map.of(
                "controlesHoy", ingresosHoy,
                "documentosVerificados", totalRegistros,
                "reportesGenerados", reporteRepository.findByRolUsuario("ROLE_PDI").size(),
                "consultasRealizadas", 0
        ));
        return ResponseEntity.ok(data);
    }

    @GetMapping("/sag")
    @PreAuthorize("hasRole('SAG')")
    public ResponseEntity<?> getDashboardSAG() {
        User user = getCurrentUser();
        Map<String, Object> data = new HashMap<>();
        data.put("user", getUserInfo(user));

        long totalInspecciones = inspeccionRepository.count();
        long cuarentena = inspeccionRepository.countByResultado("CUARENTENA");
        long aprobados = inspeccionRepository.countByResultado("APROBADO");

        data.put("stats", Map.of(
                "inspeccionesHoy", totalInspecciones,
                "certificadosEmitidos", aprobados,
                "productosCuarentena", cuarentena,
                "alertasSanitarias", inspeccionRepository.countByResultado("RECHAZADO")
        ));
        return ResponseEntity.ok(data);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("nombre", user.getNombre());
        info.put("apellido", user.getApellido());
        info.put("role", user.getRole().name());
        return info;
    }
}