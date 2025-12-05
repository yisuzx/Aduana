package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.PDIRegistro;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Service.PDIRegistroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdi")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PDI')")
public class PDIController {

    private final PDIRegistroService pdiRegistroService;
    private final ObjectMapper objectMapper;

    @PostMapping("/registro")
    public ResponseEntity<?> registroControlFronterizo(@RequestBody Map<String, Object> formData) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User agente = (User) auth.getPrincipal();

            PDIRegistro registro = new PDIRegistro();
            registro.setNombreCompleto((String) formData.get("nombreCompleto"));
            registro.setRut((String) formData.get("rut"));
            registro.setNacionalidad((String) formData.get("nacionalidad"));
            registro.setPaisProcedencia((String) formData.get("paisProcedencia"));
            registro.setPaisDestino((String) formData.get("paisDestino"));
            registro.setMotivoViaje((String) formData.get("motivoViaje"));
            registro.setMedioTransporte((String) formData.get("medioTransporte"));

            if (formData.get("fechaIngreso") != null) {
                registro.setFechaIngreso(LocalDate.parse((String) formData.get("fechaIngreso")));
            }
            if (formData.get("fechaSalida") != null) {
                registro.setFechaSalida(LocalDate.parse((String) formData.get("fechaSalida")));
            }

            if (formData.get("documentos") != null) {
                registro.setDocumentos(objectMapper.writeValueAsString(formData.get("documentos")));
            }

            registro.setObservaciones((String) formData.get("observaciones"));

            PDIRegistro guardado = pdiRegistroService.crearRegistro(agente, registro);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", guardado.getId());
            respuesta.put("fechaRegistro", guardado.getFechaRegistro().toString());
            respuesta.put("estado", guardado.getEstado());
            respuesta.put("mensaje", "Control fronterizo registrado exitosamente en la base de datos");
            respuesta.put("agente", agente.getNombre() + " " + agente.getApellido());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error en registro: " + e.getMessage()));
        }
    }

    @GetMapping("/registros")
    public ResponseEntity<?> getRegistros() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User agente = (User) auth.getPrincipal();

        var registros = pdiRegistroService.findByAgente(agente);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("total", registros.size());
        respuesta.put("registros", registros);
        respuesta.put("ingresosHoy", pdiRegistroService.contarIngresosHoy());

        return ResponseEntity.ok(respuesta);
    }
}