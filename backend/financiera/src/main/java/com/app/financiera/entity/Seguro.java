package com.app.financiera.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "seguro")
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguro")
    private int idSeguro;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_seguro")
    private TipoSeguro tipoSeguro;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_compania")
    private CompaniaSeguro compania;

    @Column(name = "numero_poliza")
    private String numeroPoliza;

    @Column(name = "numero_contrato")
    private String numeroContrato;

    @Column(name = "fecha_inicio")
    private java.sql.Date fechaInicio;

    @Column(name = "fecha_vencimiento")
    private java.sql.Date fechaVencimiento;

    @Column(name = "monto_asegurado")
    private Double montoAsegurado;

    @Column(name = "prima_mensual")
    private Double primaMensual;

    @Column(name = "prima_anual")
    private Double primaAnual;

    private Double deducible;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String beneficiarios;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String coberturas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String exclusiones;

    @Column(name = "forma_pago")
    private String formaPago;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @Column(name = "fecha_registro")
    private java.sql.Timestamp fechaRegistro;

    private String estado;
}