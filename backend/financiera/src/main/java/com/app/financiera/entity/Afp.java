package com.app.financiera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "afp")
public class Afp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_afp")
    private int idAfp;

    //Relación con institucion
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_institucion")
    private Institucion institucion;

    private String nombre;

    @Column(name = "codigo_sbs")
    private String codigoSbs;

    @Column(name = "comision_flujo")
    private Double comisionFlujo;

    @Column(name = "comision_saldo")
    private Double comisionSaldo;

    @Column(name = "comision_mixta")
    private Double comisionMixta;

    @Column(name = "rentabilidad_promedio")
    private Double rentabilidadPromedio;

    @Column(name = "fondos_disponibles", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> fondosDisponibles;

    private String estado;
}
