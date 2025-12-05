package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.Reporte;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Repository.InspeccionRepository;
import com.Sistema.Aduana.Repository.PDIRegistroRepository;
import com.Sistema.Aduana.Repository.ReporteRepository;
import com.Sistema.Aduana.Repository.TramiteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReporteRepository reporteRepository;
    private final TramiteRepository tramiteRepository;
    private final PDIRegistroRepository pdiRepository;
    private final InspeccionRepository sagRepository;
    private final ObjectMapper objectMapper;


    @GetMapping
    @PreAuthorize("hasAnyRole('ADUANA', 'PDI', 'SAG')")
    public ResponseEntity<?> getReportes(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {
        User user = getCurrentUser();
        String role = user.getRole().name();

        List<Reporte> misReportes;

        if (fechaInicio != null && fechaFin != null && !fechaInicio.isEmpty() && !fechaFin.isEmpty()) {
            try {
                LocalDateTime inicio = LocalDate.parse(fechaInicio).atStartOfDay();
                LocalDateTime fin = LocalDate.parse(fechaFin).atTime(23, 59, 59);
                misReportes = reporteRepository.findByRolUsuarioAndFechaGeneracionBetween(role, inicio, fin);
            } catch (Exception e) {
                misReportes = reporteRepository.findByRolUsuario(role);
            }
        } else {
            misReportes = reporteRepository.findByRolUsuario(role);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("user", getUserInfo(user));
        response.put("reportes", misReportes);

        Map<String, Long> datosGrafico = new HashMap<>();
        for (Reporte r : misReportes) {
            datosGrafico.put(r.getTipo(), datosGrafico.getOrDefault(r.getTipo(), 0L) + 1);
        }
        response.put("datosGrafico", datosGrafico);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", misReportes.size());
        response.put("estadisticas", stats);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/generar")
    public ResponseEntity<?> generarReporteReal(@RequestBody Map<String, String> params) {
        User user = getCurrentUser();
        String rol = user.getRole().name();

        Reporte reporte = new Reporte();
        reporte.setUsuario(user);
        reporte.setRolUsuario(rol);
        reporte.setFechaGeneracion(LocalDateTime.now());

        Map<String, Object> contenido = new HashMap<>();
        String titulo = params.getOrDefault("titulo", "Reporte General");
        String tipo = params.getOrDefault("tipo", "GENERAL");

        try {

            switch (rol) {
                case "ROLE_ADUANA":
                    long totalTramites = tramiteRepository.count();
                    long importaciones = tramiteRepository.findByTipo("IMPORTACION").size();
                    long exportaciones = tramiteRepository.findByTipo("EXPORTACION").size();

                    contenido.put("total_tramites", totalTramites);
                    contenido.put("importaciones", importaciones);
                    contenido.put("exportaciones", exportaciones);


                    if(titulo.equals("Reporte General")) titulo = "Resumen de Operaciones Aduaneras";
                    break;

                case "ROLE_PDI":
                    long totalIngresos = pdiRepository.count();
                    long hoy = pdiRepository.countByFechaIngreso(LocalDate.now());

                    contenido.put("total_historico", totalIngresos);
                    contenido.put("ingresos_hoy", hoy);

                    if(titulo.equals("Reporte General")) titulo = "Reporte de Flujo Migratorio";
                    break;

                case "ROLE_SAG":
                    long totalInspecciones = sagRepository.count();
                    long rechazados = sagRepository.countByResultado("RECHAZADO");
                    long aprobados = sagRepository.countByResultado("APROBADO");

                    contenido.put("total_inspecciones", totalInspecciones);
                    contenido.put("rechazados_alerta", rechazados);
                    contenido.put("certificados_ok", aprobados);

                    if(titulo.equals("Reporte General")) titulo = "Informe Fitosanitario";
                    break;
            }

            reporte.setTitulo(titulo);
            reporte.setTipo(tipo);
            reporte.setContenido(objectMapper.writeValueAsString(contenido)); // Convertimos Map a JSON String

            reporteRepository.save(reporte);

            return ResponseEntity.ok(Map.of("mensaje", "Reporte generado con datos reales", "id", reporte.getId()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error generando reporte: " + e.getMessage());
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("nombre", user.getNombre());
        info.put("tipo", user.getRole().name());
        return info;
    }
}