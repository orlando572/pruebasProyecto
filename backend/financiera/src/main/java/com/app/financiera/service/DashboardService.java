package com.app.financiera.service;

import java.util.HashMap;

/**
 * Servicio para gestión del Dashboard
 *
 * @author Sistema Financiero
 * @version 1.0
 */
public interface DashboardService {

    /**
     * Obtiene el resumen completo del dashboard
     * Incluye información financiera, pensiones, seguros y alertas
     */
    HashMap<String, Object> obtenerResumenCompleto(int idUsuario);

    /**
     * Obtiene información del perfil del usuario
     */
    HashMap<String, Object> obtenerPerfilUsuario(int idUsuario);

    /**
     * Obtiene las alertas activas del usuario
     */
    HashMap<String, Object> obtenerAlertas(int idUsuario);

    /**
     * Obtiene la actividad reciente del usuario
     */
    HashMap<String, Object> obtenerActividadReciente(int idUsuario, int limite);

    /**
     * Obtiene estadísticas financieras del usuario
     */
    HashMap<String, Object> obtenerEstadisticasFinancieras(int idUsuario);
}