package com.app.financiera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_fondo")
public class TipoFondo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_fondo")
    private int idTipoFondo;

    @Column(name = "nombre_tipo")
    private String nombreTipo; // "ONP", "AFP Fondo 1", etc

    private String descripcion;
    private String categoria; // "Pensiones", "Inversión"

    @Column(name = "rentabilidad_esperada")
    private Double rentabilidadEsperada;

    private String estado;
}