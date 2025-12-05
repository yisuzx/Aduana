package com.Sistema.Aduana.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspecciones_sag")
@Data
public class Inspeccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String producto;
    private String origen;

    @Column(name = "resultado")
    private String resultado; // APROBADO, RECHAZADO, CUARENTENA

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_inspeccion")
    private LocalDateTime fechaInspeccion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "inspector_id")
    private User inspector;
}