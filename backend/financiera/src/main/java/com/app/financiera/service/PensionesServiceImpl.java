package com.app.financiera.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.financiera.entity.AportePension;
import com.app.financiera.entity.SaldoPension;
import com.app.financiera.entity.ConsultaAportes;
import com.app.financiera.entity.HistorialConsultas;
import com.app.financiera.repository.ConsultaAportesRepository;
import com.app.financiera.repository.HistorialConsultasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PensionesServiceImpl implements PensionesService {

    private static final Logger logger = LoggerFactory.getLogger(PensionesServiceImpl.class);

    @Autowired
    private AportePensionService aportePensionService;

    @Autowired
    private SaldoPensionService saldoPensionService;

    @Autowired
    private ConsultaAportesRepository consultaAportesRepository;

    @Autowired
    private HistorialConsultasRepository historialConsultasRepository;

    @Override
    public HashMap<String, Object> obtenerResumenPensiones(int idUsuario) {
        logger.info("Generando resumen de pensiones para usuario: {}", idUsuario);
        HashMap<String, Object> resumen = new HashMap<>();

        try {
            Double saldoTotal = saldoPensionService.obtenerSaldoTotalUsuario(idUsuario);
            Double saldoDisponible = saldoPensionService.obtenerSaldosDisponibles(idUsuario);

            List<AportePension> aportesONP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Pensiones");
            Double montoONP = aportesONP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            List<AportePension> aportesAFP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Financiera");
            Double montoAFP = aportesAFP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            Integer años = obtenerAñosAportados(idUsuario);

            resumen.put("saldoTotal", saldoTotal != null ? saldoTotal : 0);
            resumen.put("saldoDisponible", saldoDisponible != null ? saldoDisponible : 0);
            resumen.put("estadoAFP", obtenerEstadoAFP(idUsuario));
            resumen.put("estadoONP", obtenerEstadoONP(idUsuario));
            resumen.put("aportesONP12m", montoONP);
            resumen.put("aportesAFP12m", montoAFP);
            resumen.put("años", años);
            resumen.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error generando resumen de pensiones para usuario {}: {}", idUsuario, e.getMessage());
            resumen.put("exitoso", false);
            resumen.put("error", e.getMessage());
        }

        return resumen;
    }

    @Override
    public List<AportePension> obtenerAportes(int idUsuario) {
        return aportePensionService.obtenerAportesUsuario(idUsuario);
    }

    @Override
    public List<AportePension> obtenerAportesONP(int idUsuario) {
        return aportePensionService.obtenerAportesPorSistema(idUsuario, "Pensiones");
    }

    @Override
    public List<AportePension> obtenerAportesAFP(int idUsuario) {
        return aportePensionService.obtenerAportesPorSistema(idUsuario, "Financiera");
    }

    @Override
    public AportePension crearAporte(AportePension aporte) {
        logger.info("Creando nuevo aporte para usuario: {}", aporte.getUsuario().getIdUsuario());
        return aportePensionService.guardarAporte(aporte);
    }

    @Override
    public AportePension actualizarAporte(AportePension aporte) {
        logger.info("Actualizando aporte: {}", aporte.getIdAporte());
        return aportePensionService.actualizarAporte(aporte);
    }

    @Override
    public void eliminarAporte(int idAporte) {
        logger.info("Eliminando aporte: {}", idAporte);
        aportePensionService.eliminarAporte(idAporte);
    }

    @Override
    public List<SaldoPension> obtenerSaldos(int idUsuario) {
        return saldoPensionService.obtenerSaldosUsuario(idUsuario);
    }

    @Override
    public Double obtenerSaldoTotal(int idUsuario) {
        return saldoPensionService.obtenerSaldoTotalUsuario(idUsuario);
    }

    @Override
    public Double obtenerSaldoDisponible(int idUsuario) {
        return saldoPensionService.obtenerSaldosDisponibles(idUsuario);
    }

    @Override
    public HashMap<String, Object> obtenerProyecciones(int idUsuario) {
        HashMap<String, Object> proyecciones = new HashMap<>();

        Double saldoTotal = obtenerSaldoTotal(idUsuario);
        Double proyeccionMensual = (saldoTotal != null && saldoTotal > 0) ? saldoTotal / 240 : 0;

        proyecciones.put("proyeccionMensual", proyeccionMensual);
        proyecciones.put("proyeccionConAumentoPorcentaje5", proyeccionMensual * 1.05);
        proyecciones.put("escenarioConservador", proyeccionMensual * 0.9);
        proyecciones.put("tasaAnual", 5.4);
        proyecciones.put("riesgo", "Medio");

        return proyecciones;
    }

    @Override
    public HashMap<String, Object> obtenerProyeccionPensionMensual(int idUsuario) {
        HashMap<String, Object> proyeccion = new HashMap<>();

        Double saldoTotal = obtenerSaldoTotal(idUsuario);
        Double monto = (saldoTotal != null && saldoTotal > 0) ? saldoTotal / 240 : 0;

        proyeccion.put("montoMensual", monto);
        proyeccion.put("tasaRetorno", 5.4);
        proyeccion.put("actualizado", LocalDateTime.now());

        return proyeccion;
    }

    @Override
    public ConsultaAportes registrarConsulta(ConsultaAportes consulta) {
        consulta.setFechaConsulta(LocalDateTime.now());
        logger.info("Registrando consulta para usuario: {}", consulta.getUsuario().getIdUsuario());
        return consultaAportesRepository.save(consulta);
    }

    @Override
    public List<ConsultaAportes> obtenerConsultasUsuario(int idUsuario) {
        return consultaAportesRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<ConsultaAportes> obtenerConsultasPorTipo(int idUsuario, String tipo) {
        return consultaAportesRepository.findByUsuarioAndTipo(idUsuario, tipo);
    }

    @Override
    public HistorialConsultas registrarHistorial(HistorialConsultas historial) {
        historial.setFecha(LocalDateTime.now());
        logger.info("Registrando en historial para usuario: {}", historial.getUsuario().getIdUsuario());
        return historialConsultasRepository.save(historial);
    }

    @Override
    public List<HistorialConsultas> obtenerHistorialUsuario(int idUsuario) {
        return historialConsultasRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public String obtenerEstadoAFP(int idUsuario) {
        List<AportePension> aportesAFP = obtenerAportesAFP(idUsuario);
        if (aportesAFP.isEmpty()) {
            return "Sin registro";
        }
        // Verificar si hay aportes recientes (últimos 3 meses)
        return "Activo"; // Simplificado, en producción verificar fecha
    }

    @Override
    public String obtenerEstadoONP(int idUsuario) {
        List<AportePension> aportesONP = obtenerAportesONP(idUsuario);
        if (aportesONP.isEmpty()) {
            return "Sin registro";
        }
        return "Activo";
    }

    @Override
    public Integer obtenerAñosAportados(int idUsuario) {
        List<AportePension> aportes = obtenerAportes(idUsuario);
        return (int) aportes.stream()
                .map(a -> a.getFechaAporte() != null ? a.getFechaAporte().getYear() : 0)
                .distinct()
                .count();
    }
}