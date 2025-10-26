package com.app.financiera.entity;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aporte_pension")
public class AportePension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aporte")
    private int idAporte;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion")
    private Institucion institucion;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_fondo")
    private TipoFondo tipoFondo;

    private String cuspp;
    private String periodo; // "2024-01"

    @Column(name = "monto_aporte")
    private Double montoAporte;

    @Column(name = "aporte_trabajador")
    private Double aporteTrabajador;

    @Column(name = "aporte_empleador")
    private Double aporteEmpleador;

    @Column(name = "comision_cobrada")
    private Double comisionCobrada;

    @Column(name = "seguro_invalidez")
    private Double seguroInvalidez;

    @Column(name = "fecha_aporte")
    private LocalDate fechaAporte;

    private String empleador;

    @Column(name = "ruc_empleador")
    private String rucEmpleador;

    @Column(name = "salario_declarado")
    private Double salarioDeclarado;

    @Column(name = "dias_trabajados")
    private Integer diasTrabajados;

    private String observaciones;
    private String estado; // "Registrado", "Procesado", "Observado", "Eliminado"
}