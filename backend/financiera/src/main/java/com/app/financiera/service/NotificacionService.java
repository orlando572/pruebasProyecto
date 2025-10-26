package com.app.financiera.service;

import java.util.List;

import com.app.financiera.entity.Notificacion;

public interface NotificacionService {

    Notificacion crearYEnviarNotificacion(Notificacion notificacion);

    List<Notificacion> obtenerNotificacionesUsuario(int idUsuario);

    List<Notificacion> obtenerNotificacionesNoLeidas(int idUsuario);

    Notificacion marcarComoLeida(int idNotificacion);

    long contarNotificacionesNoLeidas(int idUsuario);

    List<Notificacion> obtenerNotificacionesPorTipo(int idUsuario, String tipo);

    void eliminarNotificacion(int idNotificacion);

    void notificarVencimientoSeguro(int idUsuario, String tipoSeguro, String fechaVencimiento);

    void notificarPagoPendiente(int idUsuario, String concepto, Double monto);

    void notificarAporteRegistrado(int idUsuario, Double monto, String periodo);

    void notificarAsesoramiento(int idUsuario, String tipoAsesoramiento, String fechaHora);

    void notificarTramite(int idUsuario, String tipoTramite, String nuevoEstado);
}
