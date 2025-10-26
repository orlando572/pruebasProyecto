package com.app.financiera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "asesor")
public class Asesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asesor")
    private int idAsesor;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String especialidad; // "Pensiones", "Seguros", "Inversiones", "General"

    @Column(name = "codigo_asesor", unique = true)
    private String codigoAsesor;

    private String estado; // "Activo", "Inactivo", "Ocupado", "Disponible"

    @Column(name = "calificacion_promedio")
    private Double calificacionPromedio;

    @Column(name = "total_asesoramientos")
    private Integer totalAsesoramientos;

    @Column(name = "horario_atencion")
    private String horarioAtencion; // JSON con horarios disponibles

    @Column(name = "canales_atencion")
    private String canalesAtencion; // "Presencial,Telefónico,Virtual,Chat"

    @Column(name = "certificaciones")
    private String certificaciones; // JSON con certificaciones profesionales
}
