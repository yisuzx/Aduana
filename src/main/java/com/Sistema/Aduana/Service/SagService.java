package com.Sistema.Aduana.Service;

import com.Sistema.Aduana.Entity.Inspeccion;
import com.Sistema.Aduana.Entity.User;
import com.Sistema.Aduana.Repository.InspeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SagService {

    private final InspeccionRepository inspeccionRepository;

    public Inspeccion registrarInspeccion(Map<String, Object> datos, User inspector) {
        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setProducto((String) datos.get("producto"));
        inspeccion.setOrigen((String) datos.get("origen"));

        String resultado = (String) datos.get("resultado");
        inspeccion.setResultado(resultado != null ? resultado : "APROBADO");

        inspeccion.setObservaciones((String) datos.get("observaciones"));
        inspeccion.setFechaInspeccion(LocalDateTime.now());
        inspeccion.setInspector(inspector);

        return inspeccionRepository.save(inspeccion);
    }

    public Map<String, Object> emitirCertificado(Map<String, Object> datos, User inspector) {
        // Guardamos el registro en BD como constancia
        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setProducto((String) datos.get("producto"));
        inspeccion.setOrigen((String) datos.get("origen"));
        inspeccion.setResultado("APROBADO");
        inspeccion.setObservaciones("Certificado Fitosanitario Emitido. Tipo: " + datos.get("tipo"));
        inspeccion.setFechaInspeccion(LocalDateTime.now());
        inspeccion.setInspector(inspector);

        inspeccionRepository.save(inspeccion);

        Map<String, Object> certificado = new HashMap<>();
        certificado.put("numero", "CERT-" + System.currentTimeMillis());
        certificado.put("fechaEmision", LocalDateTime.now().toString());
        certificado.put("producto", inspeccion.getProducto());
        certificado.put("inspector", inspector.getNombre() + " " + inspector.getApellido());
        certificado.put("validoHasta", LocalDateTime.now().plusMonths(6).toString());

        return certificado;
    }

    public Map<String, Object> gestionarCuarentena(Map<String, Object> datos, User inspector) {
        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setProducto((String) datos.get("producto"));
        inspeccion.setOrigen((String) datos.get("origen"));
        inspeccion.setResultado("CUARENTENA");
        inspeccion.setObservaciones("Ingreso a cuarentena. Motivo: " + datos.get("motivo"));
        inspeccion.setFechaInspeccion(LocalDateTime.now());
        inspeccion.setInspector(inspector);

        inspeccionRepository.save(inspeccion);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("idCuarentena", "CQ-" + UUID.randomUUID().toString().substring(0, 8));
        respuesta.put("producto", inspeccion.getProducto());
        respuesta.put("estado", "EN_CUARENTENA");
        respuesta.put("fechaIngreso", inspeccion.getFechaInspeccion().toString());

        return respuesta;
    }
}