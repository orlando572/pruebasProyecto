package com.app.financiera.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@financiera.com}")
    private String fromEmail;

    @Override
    public void enviarCorreoSimple(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(destinatario);
            mailMessage.setSubject(asunto);
            mailMessage.setText(mensaje);

            mailSender.send(mailMessage);
            logger.info("Correo enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {
            logger.error("Error al enviar correo a {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    public void enviarCorreoHTML(String destinatario, String asunto, String contenidoHTML) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHTML, true);

            mailSender.send(mimeMessage);
            logger.info("Correo HTML enviado exitosamente a: {}", destinatario);
        } catch (Exception e) {
            logger.error("Error al enviar correo HTML a {}: {}", destinatario, e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionVencimientoSeguro(String destinatario, String nombreUsuario,
                                                    String tipoSeguro, String fechaVencimiento) {
        String asunto = "⚠️ Recordatorio: Vencimiento de Seguro";
        String contenido = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #ff6b6b;">Recordatorio de Vencimiento</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Le recordamos que su <strong>%s</strong> está próximo a vencer.</p>
                <p><strong>Fecha de vencimiento:</strong> %s</p>
                <p>Por favor, renueve su póliza para mantener su cobertura activa.</p>
                <hr>
                <p style="font-size: 12px; color: #666;">
                    Este es un mensaje automático del Sistema Financiero.
                </p>
            </body>
            </html>
            """, nombreUsuario, tipoSeguro, fechaVencimiento);

        enviarCorreoHTML(destinatario, asunto, contenido);
    }

    @Override
    public void enviarNotificacionPagoPendiente(String destinatario, String nombreUsuario,
                                                String concepto, Double monto) {
        String asunto = "💳 Pago Pendiente - Sistema Financiero";
        String contenido = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #4CAF50;">Pago Pendiente</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Tiene un pago pendiente:</p>
                <ul>
                    <li><strong>Concepto:</strong> %s</li>
                    <li><strong>Monto:</strong> S/ %.2f</li>
                </ul>
                <p>Por favor, realice el pago a la brevedad posible.</p>
                <hr>
                <p style="font-size: 12px; color: #666;">
                    Sistema Financiero - Gestión de Pensiones y Seguros
                </p>
            </body>
            </html>
            """, nombreUsuario, concepto, monto);

        enviarCorreoHTML(destinatario, asunto, contenido);
    }

    @Override
    public void enviarNotificacionAporteRegistrado(String destinatario, String nombreUsuario,
                                                   Double monto, String periodo) {
        String asunto = "✅ Aporte Registrado Exitosamente";
        String contenido = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #2196F3;">Aporte Registrado</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Su aporte ha sido registrado exitosamente:</p>
                <ul>
                    <li><strong>Monto:</strong> S/ %.2f</li>
                    <li><strong>Periodo:</strong> %s</li>
                </ul>
                <p>Puede consultar el detalle en su panel financiero.</p>
                <hr>
                <p style="font-size: 12px; color: #666;">
                    Sistema Financiero - Gestión de Pensiones
                </p>
            </body>
            </html>
            """, nombreUsuario, monto, periodo);

        enviarCorreoHTML(destinatario, asunto, contenido);
    }

    @Override
    public void enviarNotificacionAsesoramiento(String destinatario, String nombreUsuario,
                                                String tipoAsesoramiento, String fechaHora) {
        String asunto = "📅 Asesoramiento Programado";
        String contenido = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #9C27B0;">Asesoramiento Programado</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Su asesoramiento ha sido programado:</p>
                <ul>
                    <li><strong>Tipo:</strong> %s</li>
                    <li><strong>Fecha y Hora:</strong> %s</li>
                </ul>
                <p>Un asesor se pondrá en contacto con usted en la fecha indicada.</p>
                <hr>
                <p style="font-size: 12px; color: #666;">
                    Sistema Financiero - Asesoramiento Personalizado
                </p>
            </body>
            </html>
            """, nombreUsuario, tipoAsesoramiento, fechaHora);

        enviarCorreoHTML(destinatario, asunto, contenido);
    }

    @Override
    public void enviarNotificacionTramite(String destinatario, String nombreUsuario,
                                          String tipoTramite, String nuevoEstado) {
        String asunto = "📄 Actualización de Trámite";
        String contenido = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif;">
                <h2 style="color: #FF9800;">Actualización de Trámite</h2>
                <p>Estimado/a <strong>%s</strong>,</p>
                <p>Su trámite ha sido actualizado:</p>
                <ul>
                    <li><strong>Tipo:</strong> %s</li>
                    <li><strong>Nuevo Estado:</strong> %s</li>
                </ul>
                <p>Puede consultar más detalles en su panel de trámites.</p>
                <hr>
                <p style="font-size: 12px; color: #666;">
                    Sistema Financiero - Gestión de Trámites
                </p>
            </body>
            </html>
            """, nombreUsuario, tipoTramite, nuevoEstado);

        enviarCorreoHTML(destinatario, asunto, contenido);
    }
}
