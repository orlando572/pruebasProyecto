package com.app.financiera.service;

import java.util.HashMap;
import java.util.List;
import com.app.financiera.entity.AportePension;
import com.app.financiera.entity.SaldoPension;
import com.app.financiera.entity.ConsultaAportes;
import com.app.financiera.entity.HistorialConsultas;

public interface PensionesService {

    // RESUMEN GENERAL
    HashMap<String, Object> obtenerResumenPensiones(int idUsuario);

    // APORTES
    List<AportePension> obtenerAportes(int idUsuario);
    List<AportePension> obtenerAportesONP(int idUsuario);
    List<AportePension> obtenerAportesAFP(int idUsuario);
    AportePension crearAporte(AportePension aporte);
    AportePension actualizarAporte(AportePension aporte);
    void eliminarAporte(int idAporte);

    // SALDOS
    List<SaldoPension> obtenerSaldos(int idUsuario);
    Double obtenerSaldoTotal(int idUsuario);
    Double obtenerSaldoDisponible(int idUsuario);

    // PROYECCIONES
    HashMap<String, Object> obtenerProyecciones(int idUsuario);
    HashMap<String, Object> obtenerProyeccionPensionMensual(int idUsuario);

    // CONSULTAS (Auditoría)
    ConsultaAportes registrarConsulta(ConsultaAportes consulta);
    List<ConsultaAportes> obtenerConsultasUsuario(int idUsuario);
    List<ConsultaAportes> obtenerConsultasPorTipo(int idUsuario, String tipo);

    // HISTORIAL
    HistorialConsultas registrarHistorial(HistorialConsultas historial);
    List<HistorialConsultas> obtenerHistorialUsuario(int idUsuario);

    // ESTADO GENERAL
    String obtenerEstadoAFP(int idUsuario);
    String obtenerEstadoONP(int idUsuario);
    Integer obtenerAñosAportados(int idUsuario);
}