package com.app.financiera.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad que representa el historial de rentabilidad de fondos AFP
 * Almacena datos históricos de rendimiento para análisis y proyecciones
 *
 * @author Sistema Financiero
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "historial_rentabilidad")
public class HistorialRentabilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rentabilidad")
    private int idRentabilidad;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_afp")
    private Afp afp;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_fondo")
    private TipoFondo tipoFondo;

    private String periodo; // "YYYY-MM" formato año-mes

    @Column(name = "rentabilidad_nominal")
    private Double rentabilidadNominal; // Rentabilidad sin ajustar por inflación

    @Column(name = "rentabilidad_real")
    private Double rentabilidadReal; // Rentabilidad ajustada por inflación

    private Double patrimonio; // Patrimonio del fondo en el periodo

    @Column(name = "numero_afiliados")
    private Integer numeroAfiliados;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "rentabilidad_12_meses")
    private Double rentabilidad12Meses;

    @Column(name = "rentabilidad_24_meses")
    private Double rentabilidad24Meses;

    @Column(name = "rentabilidad_36_meses")
    private Double rentabilidad36Meses;

    @Column(name = "volatilidad")
    private Double volatilidad; // Medida de riesgo del fondo

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
