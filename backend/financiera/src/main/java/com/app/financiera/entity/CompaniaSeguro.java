package com.app.financiera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "compania_seguro")
public class CompaniaSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compania")
    private int idCompania;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion")
    private Institucion institucion;

    private String nombre;

    @Column(name = "codigo_sbs")
    private String codigoSbs;

    @Column(name = "tipos_seguro_ofrecidos", columnDefinition = "jsonb")
    private String tiposSeguroOfrecidos;

    @Column(name = "calificacion_riesgo")
    private String calificacionRiesgo;

    private String telefono;

    private String email;

    @Column(name = "sitio_web")
    private String sitioWeb;

    private String estado;
}