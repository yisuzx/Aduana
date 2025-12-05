package com.Sistema.Aduana.Controller;

import com.Sistema.Aduana.Entity.Contacto;
import com.Sistema.Aduana.Service.ContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactoService contactoService;

    @PostMapping
    public ResponseEntity<?> enviarContacto(@RequestBody Map<String, Object> contactoData) {
        try {
            Contacto contacto = new Contacto();
            contacto.setNombre((String) contactoData.get("nombre"));
            contacto.setEmail((String) contactoData.get("email"));
            contacto.setTelefono((String) contactoData.get("telefono"));
            contacto.setAsunto((String) contactoData.get("asunto"));
            contacto.setMensaje((String) contactoData.get("mensaje"));
            contacto.setTipoConsulta((String) contactoData.get("tipoConsulta"));

            Contacto guardado = contactoService.save(contacto);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("id", guardado.getId());
            respuesta.put("fecha", guardado.getFechaEnvio().toString());
            respuesta.put("estado", guardado.getEstado());
            respuesta.put("mensaje", "Mensaje de contacto recibido y guardado en la base de datos");

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al guardar contacto: " + e.getMessage()));
        }
    }
}