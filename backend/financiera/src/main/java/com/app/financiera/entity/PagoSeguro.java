package com.app.financiera.entity;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pago_seguro")
public class PagoSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private int idPago;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_seguro")
    private Seguro seguro;

    @Column(name = "numero_cuota")
    private Integer numeroCuota;

    @Column(name = "monto_pagado")
    private Double montoPagado;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    private String estado; // "Pagado", "Pendiente", "Vencido", "Parcial", "Anulado"
}