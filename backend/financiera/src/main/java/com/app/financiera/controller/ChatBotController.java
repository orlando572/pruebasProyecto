package com.app.financiera.controller;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.financiera.service.ChatBotService;
import com.app.financiera.util.AppSettings;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class ChatBotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("/mensaje")
    public ResponseEntity<?> procesarMensaje(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            String mensaje = (String) request.get("mensaje");
            Integer idUsuario = (Integer) request.get("idUsuario");

            logger.info("Procesando mensaje del chatbot para usuario: {}", idUsuario);

            if (mensaje == null || mensaje.trim().isEmpty()) {
                respuesta.put("error", true);
                respuesta.put("mensaje", "El mensaje no puede estar vacío");
                return ResponseEntity.badRequest().body(respuesta);
            }

            String respuestaBot = chatBotService.procesarMensaje(mensaje, idUsuario);

            respuesta.put("error", false);
            respuesta.put("respuesta", respuestaBot);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error procesando mensaje del chatbot: {}", e.getMessage());
            respuesta.put("error", true);
            respuesta.put("mensaje", "Lo siento, ocurrió un error. Por favor intenta nuevamente.");
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @PostMapping("/solicitar-asesor")
    public ResponseEntity<?> solicitarAsesor(@RequestBody HashMap<String, Object> request) {
        HashMap<String, Object> respuesta = new HashMap<>();

        try {
            Integer idUsuario = (Integer) request.get("idUsuario");
            String motivo = (String) request.get("motivo");

            logger.info("Solicitando asesor para usuario: {}", idUsuario);

            boolean exito = chatBotService.solicitarContactoAsesor(idUsuario, motivo);

            if (exito) {
                respuesta.put("error", false);
                respuesta.put("mensaje", "¡Solicitud enviada! Un asesor se comunicará contigo pronto por correo o teléfono.");
            } else {
                respuesta.put("error", true);
                respuesta.put("mensaje", "No se pudo procesar la solicitud. Intenta nuevamente.");
            }

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error solicitando asesor: {}", e.getMessage());
            respuesta.put("error", true);
            respuesta.put("mensaje", "Error al procesar la solicitud");
            return ResponseEntity.status(500).body(respuesta);
        }
    }
}