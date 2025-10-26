package com.app.financiera.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.financiera.entity.*;
import com.app.financiera.repository.*;

/**
 * Implementación del servicio de Dashboard
 *
 * @author Sistema Financiero
 * @version 1.0
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AportePensionRepository aportePensionRepository;

    @Autowired
    private SaldoPensionRepository saldoPensionRepository;

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private PagoSeguroRepository pagoSeguroRepository;

    @Autowired
    private TramiteSeguroRepository tramiteSeguroRepository;

    @Autowired
    private HistorialConsultasRepository historialConsultasRepository;

    @Override
    public HashMap<String, Object> obtenerResumenCompleto(int idUsuario) {
        logger.info("Generando resumen completo de dashboard para usuario: {}", idUsuario);

        HashMap<String, Object> resumen = new HashMap<>();

        try {
            // Información del usuario
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (!usuarioOpt.isPresent()) {
                resumen.put("exitoso", false);
                resumen.put("mensaje", "Usuario no encontrado");
                return resumen;
            }

            Usuario usuario = usuarioOpt.get();

            // SECCIÓN 1: Información Personal
            HashMap<String, Object> infoPersonal = new HashMap<>();
            infoPersonal.put("nombre", usuario.getNombre() + " " + usuario.getApellido());
            infoPersonal.put("dni", usuario.getDni());
            infoPersonal.put("correo", usuario.getCorreo());
            infoPersonal.put("telefono", usuario.getTelefono());
            infoPersonal.put("fotoPerfil", usuario.getFotoPerfil());
            infoPersonal.put("rol", usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Usuario");
            infoPersonal.put("ultimoAcceso", usuario.getUltimoAcceso());
            resumen.put("infoPersonal", infoPersonal);

            // SECCIÓN 2: Resumen Financiero
            HashMap<String, Object> resumenFinanciero = new HashMap<>();

            // Saldos de pensión
            Double saldoTotal = saldoPensionRepository.sumSaldosUsuario(idUsuario);
            Double saldoDisponible = saldoPensionRepository.sumSaldosDisponibles(idUsuario);
            resumenFinanciero.put("saldoTotal", saldoTotal != null ? saldoTotal : 0);
            resumenFinanciero.put("saldoDisponible", saldoDisponible != null ? saldoDisponible : 0);

            // Aportes del año actual
            int yearActual = Calendar.getInstance().get(Calendar.YEAR);
            Double aportesYear = aportePensionRepository.sumAportesUsuarioYear(idUsuario, yearActual);
            resumenFinanciero.put("aportesYearActual", aportesYear != null ? aportesYear : 0);

            // Proyección de pensión mensual
            Double proyeccionMensual = saldoTotal != null && saldoTotal > 0 ? saldoTotal / 240 : 0;
            resumenFinanciero.put("proyeccionPensionMensual", proyeccionMensual);

            // Rentabilidad promedio (simplificado)
            resumenFinanciero.put("rentabilidadPromedio", 5.4);

            resumen.put("resumenFinanciero", resumenFinanciero);

            // SECCIÓN 3: Resumen de Seguros
            HashMap<String, Object> resumenSeguros = new HashMap<>();

            List<Seguro> seguros = seguroRepository.findByUsuario(idUsuario);
            long segurosActivos = seguros.stream()
                    .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                    .count();

            Double primaMensualTotal = seguros.stream()
                    .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                    .mapToDouble(s -> s.getPrimaMensual() != null ? s.getPrimaMensual() : 0)
                    .sum();

            Double coberturaTotal = seguros.stream()
                    .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                    .mapToDouble(s -> s.getMontoAsegurado() != null ? s.getMontoAsegurado() : 0)
                    .sum();

            resumenSeguros.put("totalSeguros", seguros.size());
            resumenSeguros.put("segurosActivos", segurosActivos);
            resumenSeguros.put("primaMensualTotal", primaMensualTotal);
            resumenSeguros.put("coberturaTotal", coberturaTotal);

            resumen.put("resumenSeguros", resumenSeguros);

            // SECCIÓN 4: Alertas
            HashMap<String, Object> alertas = obtenerAlertas(idUsuario);
            resumen.put("alertas", alertas);

            // SECCIÓN 5: Actividad Reciente
            HashMap<String, Object> actividad = obtenerActividadReciente(idUsuario, 5);
            resumen.put("actividadReciente", actividad);

            // SECCIÓN 6: Estadísticas por Año
            List<HashMap<String, Object>> estadisticasYear = new ArrayList<>();
            for (int i = 2; i >= 0; i--) {
                int year = yearActual - i;
                Double totalYear = aportePensionRepository.sumAportesUsuarioYear(idUsuario, year);

                HashMap<String, Object> yearData = new HashMap<>();
                yearData.put("year", year);
                yearData.put("total", totalYear != null ? totalYear : 0.0);
                yearData.put("totalAportes", totalYear != null ? totalYear : 0.0); // Ambos nombres por compatibilidad
                estadisticasYear.add(yearData);
            }
            resumen.put("estadisticasYear", estadisticasYear);

            logger.info("Estadísticas por año generadas: {}", estadisticasYear);

            // SECCIÓN 7: Información de Pensiones
            HashMap<String, Object> infoPensiones = new HashMap<>();

            String tipoRegimen = usuario.getTipoRegimen();
            String nombreAfp = usuario.getAfp() != null ? usuario.getAfp().getNombre() : "No afiliado";

            infoPensiones.put("tipoRegimen", tipoRegimen != null ? tipoRegimen : "No especificado");
            infoPensiones.put("afp", nombreAfp);
            infoPensiones.put("cuspp", usuario.getCuspp());
            infoPensiones.put("fechaAfiliacion", usuario.getFechaAfiliacion());

            resumen.put("infoPensiones", infoPensiones);

            resumen.put("exitoso", true);
            resumen.put("fechaGeneracion", LocalDateTime.now());

            logger.info("Resumen de dashboard generado exitosamente para usuario: {}", idUsuario);

        } catch (Exception e) {
            logger.error("Error al generar resumen de dashboard: {}", e.getMessage(), e);
            resumen.put("exitoso", false);
            resumen.put("mensaje", "Error al generar el resumen del dashboard");
            resumen.put("error", e.getMessage());
        }

        return resumen;
    }

    @Override
    public HashMap<String, Object> obtenerPerfilUsuario(int idUsuario) {
        HashMap<String, Object> perfil = new HashMap<>();

        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (!usuarioOpt.isPresent()) {
                perfil.put("exitoso", false);
                perfil.put("mensaje", "Usuario no encontrado");
                return perfil;
            }

            Usuario usuario = usuarioOpt.get();

            perfil.put("idUsuario", usuario.getIdUsuario());
            perfil.put("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
            perfil.put("dni", usuario.getDni());
            perfil.put("correo", usuario.getCorreo());
            perfil.put("telefono", usuario.getTelefono());
            perfil.put("direccion", usuario.getDireccion());
            perfil.put("distrito", usuario.getDistrito());
            perfil.put("provincia", usuario.getProvincia());
            perfil.put("departamento", usuario.getDepartamento());
            perfil.put("fechaNacimiento", usuario.getFechaNacimiento());
            perfil.put("genero", usuario.getGenero());
            perfil.put("estadoCivil", usuario.getEstadoCivil());
            perfil.put("fotoPerfil", usuario.getFotoPerfil());
            perfil.put("rol", usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Usuario");
            perfil.put("estado", usuario.getEstado());
            perfil.put("ultimoAcceso", usuario.getUltimoAcceso());

            // Información laboral
            perfil.put("centroTrabajo", usuario.getCentroTrabajo());
            perfil.put("salarioActual", usuario.getSalarioActual());
            perfil.put("tipoContrato", usuario.getTipoContrato());

            perfil.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error al obtener perfil de usuario: {}", e.getMessage(), e);
            perfil.put("exitoso", false);
            perfil.put("mensaje", "Error al obtener el perfil del usuario");
        }

        return perfil;
    }

    @Override
    public HashMap<String, Object> obtenerAlertas(int idUsuario) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<HashMap<String, Object>> alertas = new ArrayList<>();
        int totalAlertas = 0;

        try {
            // ALERTA 1: Seguros próximos a vencer (30 días)
            List<Seguro> segurosProximosVencer = seguroRepository.findByUsuario(idUsuario).stream()
                    .filter(s -> {
                        if (s.getFechaVencimiento() != null &&
                                ("Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))) {
                            LocalDate fechaVenc = s.getFechaVencimiento().toLocalDate();
                            LocalDate hoy = LocalDate.now();
                            long diasParaVencer = ChronoUnit.DAYS.between(hoy, fechaVenc);
                            return diasParaVencer >= 0 && diasParaVencer <= 30;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            if (!segurosProximosVencer.isEmpty()) {
                HashMap<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "warning");
                alerta.put("titulo", "Seguros próximos a vencer");
                alerta.put("mensaje", segurosProximosVencer.size() + " seguro(s) vencen en los próximos 30 días");
                alerta.put("cantidad", segurosProximosVencer.size());
                alerta.put("icono", "AlertTriangle");
                alertas.add(alerta);
                totalAlertas += segurosProximosVencer.size();
            }

            // ALERTA 2: Pagos pendientes
            List<PagoSeguro> pagosPendientes = pagoSeguroRepository.findPendientesByUsuario(idUsuario);
            if (!pagosPendientes.isEmpty()) {
                Double montoPendiente = pagosPendientes.stream()
                        .mapToDouble(p -> p.getMontoPagado() != null ? p.getMontoPagado() : 0)
                        .sum();

                HashMap<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "danger");
                alerta.put("titulo", "Pagos pendientes");
                alerta.put("mensaje", "Tienes " + pagosPendientes.size() + " pago(s) pendiente(s)");
                alerta.put("cantidad", pagosPendientes.size());
                alerta.put("monto", montoPendiente);
                alerta.put("icono", "CreditCard");
                alertas.add(alerta);
                totalAlertas += pagosPendientes.size();
            }

            // ALERTA 3: Trámites pendientes
            long tramitesPendientes = tramiteSeguroRepository.countPendientesByUsuario(idUsuario);
            if (tramitesPendientes > 0) {
                HashMap<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "info");
                alerta.put("titulo", "Trámites en proceso");
                alerta.put("mensaje", tramitesPendientes + " trámite(s) en proceso o pendiente(s)");
                alerta.put("cantidad", tramitesPendientes);
                alerta.put("icono", "FileText");
                alertas.add(alerta);
                totalAlertas += tramitesPendientes;
            }

            // ALERTA 4: Sin aportes recientes (últimos 3 meses)
            LocalDate hace3Meses = LocalDate.now().minusMonths(3);
            List<AportePension> aportesRecientes = aportePensionRepository.findByUsuarioId(idUsuario).stream()
                    .filter(a -> a.getFechaAporte() != null && a.getFechaAporte().isAfter(hace3Meses))
                    .collect(Collectors.toList());

            if (aportesRecientes.isEmpty()) {
                HashMap<String, Object> alerta = new HashMap<>();
                alerta.put("tipo", "warning");
                alerta.put("titulo", "Sin aportes recientes");
                alerta.put("mensaje", "No se han registrado aportes en los últimos 3 meses");
                alerta.put("icono", "AlertCircle");
                alertas.add(alerta);
                totalAlertas++;
            }

            resultado.put("alertas", alertas);
            resultado.put("totalAlertas", totalAlertas);
            resultado.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error al obtener alertas: {}", e.getMessage(), e);
            resultado.put("exitoso", false);
            resultado.put("mensaje", "Error al obtener alertas");
        }

        return resultado;
    }

    @Override
    public HashMap<String, Object> obtenerActividadReciente(int idUsuario, int limite) {
        HashMap<String, Object> resultado = new HashMap<>();
        List<HashMap<String, Object>> actividades = new ArrayList<>();

        try {
            // Combinar diferentes fuentes de actividad
            List<HistorialConsultas> historial = historialConsultasRepository.findByUsuarioId(idUsuario);

            // Convertir historial a actividades
            for (HistorialConsultas h : historial) {
                if (actividades.size() >= limite) break;

                HashMap<String, Object> actividad = new HashMap<>();
                actividad.put("tipo", h.getTipoConsulta());
                actividad.put("descripcion", h.getDetalleConsulta());
                actividad.put("fecha", h.getFecha());
                actividad.put("resultado", h.getResultado());
                actividad.put("icono", getIconoActividad(h.getTipoConsulta()));
                actividades.add(actividad);
            }

            // Agregar aportes recientes
            List<AportePension> aportesRecientes = aportePensionRepository.findByUsuarioId(idUsuario);
            for (int i = 0; i < Math.min(2, aportesRecientes.size()); i++) {
                if (actividades.size() >= limite) break;

                AportePension a = aportesRecientes.get(i);
                HashMap<String, Object> actividad = new HashMap<>();
                actividad.put("tipo", "Aporte");
                actividad.put("descripcion", "Aporte registrado: " + a.getPeriodo());
                actividad.put("fecha", a.getFechaAporte());
                actividad.put("monto", a.getMontoAporte());
                actividad.put("icono", "DollarSign");
                actividades.add(actividad);
            }

            // Ordenar por fecha descendente
            actividades.sort((a1, a2) -> {
                Object fecha1 = a1.get("fecha");
                Object fecha2 = a2.get("fecha");
                if (fecha1 instanceof LocalDateTime && fecha2 instanceof LocalDateTime) {
                    return ((LocalDateTime) fecha2).compareTo((LocalDateTime) fecha1);
                } else if (fecha1 instanceof LocalDate && fecha2 instanceof LocalDate) {
                    return ((LocalDate) fecha2).compareTo((LocalDate) fecha1);
                }
                return 0;
            });

            // Limitar resultados
            if (actividades.size() > limite) {
                actividades = actividades.subList(0, limite);
            }

            resultado.put("actividades", actividades);
            resultado.put("total", actividades.size());
            resultado.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error al obtener actividad reciente: {}", e.getMessage(), e);
            resultado.put("exitoso", false);
            resultado.put("mensaje", "Error al obtener actividad reciente");
        }

        return resultado;
    }

    @Override
    public HashMap<String, Object> obtenerEstadisticasFinancieras(int idUsuario) {
        HashMap<String, Object> estadisticas = new HashMap<>();

        try {
            int yearActual = Calendar.getInstance().get(Calendar.YEAR);

            // Aportes por sistema
            List<AportePension> aportesONP = aportePensionRepository
                    .findByUsuarioAndSistema(idUsuario, "Pensiones");
            List<AportePension> aportesAFP = aportePensionRepository
                    .findByUsuarioAndSistema(idUsuario, "Financiera");

            Double montoONP = aportesONP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0)
                    .sum();

            Double montoAFP = aportesAFP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0)
                    .sum();

            Double totalAportes = montoONP + montoAFP;

            // Distribución de aportes
            HashMap<String, Object> distribucion = new HashMap<>();
            distribucion.put("onp", montoONP);
            distribucion.put("afp", montoAFP);
            distribucion.put("total", totalAportes);
            distribucion.put("porcentajeONP", totalAportes > 0 ? (montoONP / totalAportes) * 100 : 0);
            distribucion.put("porcentajeAFP", totalAportes > 0 ? (montoAFP / totalAportes) * 100 : 0);

            estadisticas.put("distribucionAportes", distribucion);

            // Tendencia de aportes (últimos 3 años)
            List<HashMap<String, Object>> tendencia = new ArrayList<>();
            for (int i = 2; i >= 0; i--) {
                int year = yearActual - i;
                Double total = aportePensionRepository.sumAportesUsuarioYear(idUsuario, year);

                HashMap<String, Object> yearData = new HashMap<>();
                yearData.put("year", year);
                yearData.put("total", total != null ? total : 0);
                tendencia.add(yearData);
            }
            estadisticas.put("tendenciaAportes", tendencia);

            // Comparativo año actual vs anterior
            Double aportesYearActual = aportePensionRepository
                    .sumAportesUsuarioYear(idUsuario, yearActual);
            Double aportesYearAnterior = aportePensionRepository
                    .sumAportesUsuarioYear(idUsuario, yearActual - 1);

            HashMap<String, Object> comparativo = new HashMap<>();
            comparativo.put("yearActual", yearActual);
            comparativo.put("montoYearActual", aportesYearActual != null ? aportesYearActual : 0);
            comparativo.put("yearAnterior", yearActual - 1);
            comparativo.put("montoYearAnterior", aportesYearAnterior != null ? aportesYearAnterior : 0);

            if (aportesYearAnterior != null && aportesYearAnterior > 0 && aportesYearActual != null) {
                double variacion = ((aportesYearActual - aportesYearAnterior) / aportesYearAnterior) * 100;
                comparativo.put("variacionPorcentual", variacion);
                comparativo.put("tendencia", variacion >= 0 ? "positiva" : "negativa");
            }

            estadisticas.put("comparativoAnual", comparativo);
            estadisticas.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error al obtener estadísticas financieras: {}", e.getMessage(), e);
            estadisticas.put("exitoso", false);
            estadisticas.put("mensaje", "Error al obtener estadísticas financieras");
        }

        return estadisticas;
    }

    /**
     * Método auxiliar para obtener icono según tipo de actividad
     */
    private String getIconoActividad(String tipoConsulta) {
        switch (tipoConsulta != null ? tipoConsulta : "") {
            case "Aporte": return "DollarSign";
            case "Seguro": return "Shield";
            case "Rentabilidad": return "TrendingUp";
            case "Proyección": return "BarChart";
            default: return "Activity";
        }
    }
}