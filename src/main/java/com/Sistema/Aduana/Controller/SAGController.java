package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.Inspeccion;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Service.SagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sag")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SAG')")
public class SAGController {

    private final SagService sagService;

    @PostMapping("/inspeccion")
    public ResponseEntity<?> registrarInspeccion(@RequestBody Map<String, Object> datos) {
        try {
            User inspector = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Inspeccion guardado = sagService.registrarInspeccion(datos, inspector);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("idInspeccion", guardado.getId());
            respuesta.put("fecha", guardado.getFechaInspeccion().toString());
            respuesta.put("resultado", guardado.getResultado());
            respuesta.put("mensaje", "Inspección registrada en base de datos.");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/certificacion")
    public ResponseEntity<?> emitirCertificado(@RequestBody Map<String, Object> datos) {
        try {
            User inspector = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(sagService.emitirCertificado(datos, inspector));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/cuarentena")
    public ResponseEntity<?> gestionarCuarentena(@RequestBody Map<String, Object> datos) {
        try {
            User inspector = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.ok(sagService.gestionarCuarentena(datos, inspector));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}