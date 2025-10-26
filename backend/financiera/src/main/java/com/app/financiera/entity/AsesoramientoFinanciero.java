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
 * Entidad que representa un asesoramiento financiero realizado
 * Registra las consultas y asesorías brindadas a los usuarios
 *
 * @author Sistema Financiero
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "asesoramiento_financiero")
public class AsesoramientoFinanciero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asesoramiento")
    private int idAsesoramiento;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asesor")
    private Asesor asesor;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aporte")
    private AportePension aporte;

    @Column(name = "tipo_asesoramiento")
    private String tipoAsesoramiento; // "Pensiones", "Seguros", "Inversiones", "Trámites", "Consulta General"

    @Column(name = "canal_atencion")
    private String canalAtencion; // "Presencial", "Telefónico", "Virtual", "Chat"

    @Column(name = "fecha_asesoramiento")
    private LocalDateTime fechaAsesoramiento;

    @Column(name = "seguimiento_requerido")
    private Boolean seguimientoRequerido;

    @Column(name = "fecha_seguimiento")
    private LocalDateTime fechaSeguimiento;

    @Column(name = "satisfaccion_cliente")
    private Integer satisfaccionCliente; // 1-5

    @Column(columnDefinition = "TEXT")
    private String comentarios;

    private String estado; // "Programado", "En Proceso", "Completado", "Cancelado"

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "temas_tratados", columnDefinition = "TEXT")
    private String temasTratados; // JSON con lista de temas

    @Column(name = "recomendaciones", columnDefinition = "TEXT")
    private String recomendaciones;

    @PrePersist
    protected void onCreate() {
        if (fechaAsesoramiento == null) {
            fechaAsesoramiento = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "Programado";
        }
        if (seguimientoRequerido == null) {
            seguimientoRequerido = false;
        }
    }
}
