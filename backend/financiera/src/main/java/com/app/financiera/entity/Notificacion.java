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
 * Entidad que representa una notificación del sistema
 * Gestiona alertas, recordatorios y mensajes para los usuarios
 *
 * @author Sistema Financiero
 * @version 1.0
 */
@Getter
@Setter
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private int idNotificacion;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "tipo_notificacion")
    private String tipoNotificacion; // "Alerta", "Recordatorio", "Informativa", "Urgente"

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    private String canal; // "Email", "SMS", "Push", "Sistema"

    private String prioridad; // "Alta", "Media", "Baja"

    private String estado; // "Pendiente", "Enviada", "Leída", "Error"

    @Column(name = "asunto")
    private String asunto;

    @Column(name = "url_accion")
    private String urlAccion; // URL para redirigir al usuario

    @Column(name = "datos_adicionales", columnDefinition = "TEXT")
    private String datosAdicionales; // JSON con información extra

    @PrePersist
    protected void onCreate() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        if (estado == null) {
            estado = "Pendiente";
        }
        if (prioridad == null) {
            prioridad = "Media";
        }
    }
}
