package com.Sistema.Aduana.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pdi_registros")
@Data
public class PDIRegistro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    private String rut;

    private String nacionalidad;

    @Column(name = "pais_procedencia")
    private String paisProcedencia;

    @Column(name = "pais_destino")
    private String paisDestino;

    @Column(name = "motivo_viaje")
    private String motivoViaje = "turismo";

    @Column(name = "medio_transporte")
    private String medioTransporte = "automovil";

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(columnDefinition = "JSON")
    private String documentos;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "agente_id")
    private User agente;

    private String estado = "REGISTRADO";

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}