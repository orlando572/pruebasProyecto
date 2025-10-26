package com.app.financiera.entity;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;

    // Relación con la tabla rol Usuario
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private RolUsuario rol;

    private String nombre;
    private String apellido;
    private String dni;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento")
    private Date fechaNacimiento;

    private String genero;

    @Column(name = "estado_civil")
    private String estadoCivil;

    private String correo;
    private String telefono;
    private String direccion;
    private String distrito;
    private String provincia;
    private String departamento;
    private String contrasena;

    @Column(name = "clave_sol")
    private String claveSol;

    @Column(name = "centro_trabajo")
    private String centroTrabajo;

    @Column(name = "salario_actual")
    private Double salarioActual;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_ingreso_trabajo")
    private Date fechaIngresoTrabajo;

    @Column(name = "tipo_contrato")
    private String tipoContrato;

    private String cuspp;

    // Relación con AFP (opcional)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_afp", nullable = true)
    private Afp afp;

    @Column(name = "tipo_regimen")
    private String tipoRegimen;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Lima")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_afiliacion")
    private Date fechaAfiliacion;

    @Column(name = "notificaciones_email")
    private boolean notificacionesEmail;

    @Column(name = "notificaciones_sms")
    private boolean notificacionesSms;

    private String estado = "Activo";

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Lima")
    @Column(name = "ultimo_acceso")
    private Date ultimoAcceso;

    // NUEVO CAMPO: Foto de perfil
    @Column(name = "foto_perfil", columnDefinition = "TEXT")
    private String fotoPerfil;
}