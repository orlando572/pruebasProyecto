package com.app.financiera.controller;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.financiera.service.DashboardService;
import com.app.financiera.util.AppSettings;

/**
 * Controlador REST para el Dashboard
 * Proporciona información consolidada del usuario
 *
 * @author Sistema Financiero
 * @version 1.0
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private DashboardService dashboardService;

    /**
     * Obtiene el resumen completo del dashboard para el usuario
     * Incluye información financiera, pensiones, seguros y alertas
     *
     * @param idUsuario ID del usuario
     * @return ResponseEntity con el resumen completo del dashboard
     */
    @GetMapping("/resumen/{idUsuario}")
    public ResponseEntity<?> obtenerResumenDashboard(@PathVariable int idUsuario) {
        logger.info("Solicitud de resumen de dashboard para usuario: {}", idUsuario);
        try {
            HashMap<String, Object> resumen = dashboardService.obtenerResumenCompleto(idUsuario);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error al obtener resumen de dashboard: {}", e.getMessage(), e);
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al obtener el resumen del dashboard");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Obtiene información del perfil del usuario
     *
     * @param idUsuario ID del usuario
     * @return ResponseEntity con información del perfil
     */
    @GetMapping("/perfil/{idUsuario}")
    public ResponseEntity<?> obtenerPerfilUsuario(@PathVariable int idUsuario) {
        logger.info("Solicitud de perfil para dashboard, usuario: {}", idUsuario);
        try {
            HashMap<String, Object> perfil = dashboardService.obtenerPerfilUsuario(idUsuario);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            logger.error("Error al obtener perfil de usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener el perfil del usuario");
        }
    }

    /**
     * Obtiene las alertas activas del usuario
     *
     * @param idUsuario ID del usuario
     * @return ResponseEntity con lista de alertas
     */
    @GetMapping("/alertas/{idUsuario}")
    public ResponseEntity<?> obtenerAlertas(@PathVariable int idUsuario) {
        logger.info("Solicitud de alertas para usuario: {}", idUsuario);
        try {
            HashMap<String, Object> alertas = dashboardService.obtenerAlertas(idUsuario);
            return ResponseEntity.ok(alertas);
        } catch (Exception e) {
            logger.error("Error al obtener alertas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener alertas");
        }
    }

    /**
     * Obtiene actividad reciente del usuario
     *
     * @param idUsuario ID del usuario
     * @param limite Cantidad de registros a retornar (opcional, default 5)
     * @return ResponseEntity con actividad reciente
     */
    @GetMapping("/actividad/{idUsuario}")
    public ResponseEntity<?> obtenerActividadReciente(
            @PathVariable int idUsuario,
            @RequestParam(defaultValue = "5") int limite) {
        logger.info("Solicitud de actividad reciente para usuario: {}, límite: {}", idUsuario, limite);
        try {
            HashMap<String, Object> actividad = dashboardService.obtenerActividadReciente(idUsuario, limite);
            return ResponseEntity.ok(actividad);
        } catch (Exception e) {
            logger.error("Error al obtener actividad reciente: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener actividad reciente");
        }
    }

    /**
     * Obtiene estadísticas financieras del usuario
     *
     * @param idUsuario ID del usuario
     * @return ResponseEntity con estadísticas financieras
     */
    @GetMapping("/estadisticas/{idUsuario}")
    public ResponseEntity<?> obtenerEstadisticasFinancieras(@PathVariable int idUsuario) {
        logger.info("Solicitud de estadísticas financieras para usuario: {}", idUsuario);
        try {
            HashMap<String, Object> estadisticas = dashboardService.obtenerEstadisticasFinancieras(idUsuario);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener estadísticas financieras");
        }
    }

    /**
     * Health check del dashboard
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        HashMap<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("servicio", "Dashboard");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
}