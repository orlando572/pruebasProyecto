package com.app.financiera.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rol_usuario")
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private int idRol;

    @Column(name = "nombre_rol")
    private String nombreRol;

    private String descripcion;

    // Campo tipo JSON
    @Column(columnDefinition = "JSON")
    private String permisos;

    private String estado;
}
