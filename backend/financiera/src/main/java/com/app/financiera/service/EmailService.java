package com.app.financiera.service;

public interface EmailService {

    void enviarCorreoSimple(String destinatario, String asunto, String mensaje);

    void enviarCorreoHTML(String destinatario, String asunto, String contenidoHTML);

    void enviarNotificacionVencimientoSeguro(String destinatario, String nombreUsuario,
                                             String tipoSeguro, String fechaVencimiento);

    void enviarNotificacionPagoPendiente(String destinatario, String nombreUsuario,
                                         String concepto, Double monto);

    void enviarNotificacionAporteRegistrado(String destinatario, String nombreUsuario,
                                            Double monto, String periodo);

    void enviarNotificacionAsesoramiento(String destinatario, String nombreUsuario,
                                         String tipoAsesoramiento, String fechaHora);

    void enviarNotificacionTramite(String destinatario, String nombreUsuario,
                                   String tipoTramite, String nuevoEstado);
}
