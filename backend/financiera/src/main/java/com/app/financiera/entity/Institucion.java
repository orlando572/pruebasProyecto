package com.app.financiera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "institucion")
public class Institucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_institucion")
    private int idInstitucion;

    private String nombre;
    private String tipo;
    private String ruc;
    private String direccion;
    private String telefono;
    private String email;

    @Column(name = "sitio_web")
    private String sitioWeb;

    private String estado;
}
