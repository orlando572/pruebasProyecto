package com.app.financiera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_seguro")
public class TipoSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_seguro")
    private int idTipoSeguro;

    private String nombre;

    private String descripcion;

    private String categoria; // "Vehicular", "Hogar", "Salud", "Vida"

    @Column(name = "cobertura_principal")
    private String coberturaPrincipal;

    private String estado;
}