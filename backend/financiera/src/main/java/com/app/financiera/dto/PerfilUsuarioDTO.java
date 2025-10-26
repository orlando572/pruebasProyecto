package com.app.financiera.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class PerfilUsuarioDTO {
    private int idUsuario;

    // Información Personal
    private String nombre;
    private String apellido;
    private String dni;
    private Date fechaNacimiento;
    private String genero;
    private String estadoCivil;

    // Información de Contacto
    private String correo;
    private String telefono;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;

    // Información Laboral
    private String centroTrabajo;
    private Double salarioActual;
    private Date fechaIngresoTrabajo;
    private String tipoContrato;

    // Información de Pensiones
    private String cuspp;
    private Integer idAfp;
    private String tipoRegimen;
    private Date fechaAfiliacion;

    // Preferencias
    private boolean notificacionesEmail;
    private boolean notificacionesSms;

    // Foto de Perfil
    private String fotoPerfil;

    // Campos de solo lectura
    private String nombreRol;
    private String nombreAfp;
    private String estado;
}