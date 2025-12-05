package com.Sistema.Aduana.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADUANA')")
public class DocumentoController {

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getDocumentos() {

        List<Map<String, Object>> docs = List.of(
                Map.of("id", 101, "nombre", "Certificado de Origen", "tipo", "Certificado", "fechaCarga", LocalDateTime.now().minusDays(5).toString(), "estado", "APROBADO"),
                Map.of("id", 102, "nombre", "Factura de Venta", "tipo", "Factura", "fechaCarga", LocalDateTime.now().minusDays(2).toString(), "estado", "PENDIENTE")
        );
        return ResponseEntity.ok(docs);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(@RequestParam("file") MultipartFile file,
                                             @RequestParam("descripcion") String descripcion) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Documento subido exitosamente.",
                "filename", file.getOriginalFilename(),
                "size", file.getSize(),
                "descripcion", descripcion
        ));
    }
}