package com.app.financiera.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TramiteSeguro;
import com.app.financiera.entity.BeneficiarioSeguro;
import com.app.financiera.entity.PagoSeguro;
import com.app.financiera.repository.SeguroRepository;
import com.app.financiera.repository.TramiteSeguroRepository;
import com.app.financiera.repository.BeneficiarioSeguroRepository;
import com.app.financiera.repository.PagoSeguroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SegurosServiceImpl implements SegurosService {

    private static final Logger logger = LoggerFactory.getLogger(SegurosServiceImpl.class);

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private TramiteSeguroRepository tramiteRepository;

    @Autowired
    private BeneficiarioSeguroRepository beneficiarioRepository;

    @Autowired
    private PagoSeguroRepository pagoRepository;

    @Override
    public HashMap<String, Object> obtenerResumenAdministrativo(int idUsuario) {
        logger.info("Generando resumen administrativo de seguros para usuario: {}", idUsuario);

        HashMap<String, Object> resumen = new HashMap<>();

        try {
            List<Seguro> segurosActivos = obtenerSegurosActivos(idUsuario);
            long totalBeneficiarios = segurosActivos.stream()
                    .mapToLong(s -> beneficiarioRepository.countBySeguroId(s.getIdSeguro()))
                    .sum();

            Double pagosPendientes = pagoRepository.sumMontosPendientesByUsuario(idUsuario);
            long tramitesPendientes = tramiteRepository.countPendientesByUsuario(idUsuario);

            // Calcular alertas (vencimientos en 30 días + trámites pendientes)
            long alertasVencimiento = obtenerSegurosProximosVencer(idUsuario, 30).size();
            long totalAlertas = alertasVencimiento + tramitesPendientes;

            resumen.put("polizasActivas", segurosActivos.size());
            resumen.put("beneficiariosRegistrados", totalBeneficiarios);
            resumen.put("pagosPendientes", pagosPendientes != null ? pagosPendientes : 0);
            resumen.put("alertas", totalAlertas);
            resumen.put("alertasVencimiento", alertasVencimiento);
            resumen.put("tramitesPendientes", tramitesPendientes);
            resumen.put("exitoso", true);

        } catch (Exception e) {
            logger.error("Error generando resumen administrativo: {}", e.getMessage());
            resumen.put("exitoso", false);
            resumen.put("error", e.getMessage());
        }

        return resumen;
    }

    @Override
    public List<Seguro> obtenerSegurosUsuario(int idUsuario) {
        return seguroRepository.findByUsuario(idUsuario);
    }

    @Override
    public List<Seguro> obtenerSegurosActivos(int idUsuario) {
        return seguroRepository.findByUsuario(idUsuario).stream()
                .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public Seguro obtenerSeguroPorId(int idSeguro) {
        return seguroRepository.findById(idSeguro).orElse(null);
    }

    @Override
    public Seguro crearSeguro(Seguro seguro) {
        logger.info("Creando nuevo seguro para usuario: {}", seguro.getUsuario().getIdUsuario());
        if (seguro.getFechaRegistro() == null) {
            seguro.setFechaRegistro(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        return seguroRepository.save(seguro);
    }

    @Override
    public Seguro actualizarSeguro(Seguro seguro) {
        logger.info("Actualizando seguro ID: {}", seguro.getIdSeguro());
        return seguroRepository.save(seguro);
    }

    @Override
    public void eliminarSeguro(int idSeguro) {
        logger.info("Eliminando seguro ID: {}", idSeguro);
        Seguro seguro = obtenerSeguroPorId(idSeguro);
        if (seguro != null) {
            seguro.setEstado("Cancelado");
            seguroRepository.save(seguro);
        }
    }

    @Override
    public List<Seguro> obtenerSegurosProximosVencer(int idUsuario, int dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return obtenerSegurosActivos(idUsuario).stream()
                .filter(s -> {
                    if (s.getFechaVencimiento() != null) {
                        LocalDate vencimiento = s.getFechaVencimiento().toLocalDate();
                        return !vencimiento.isBefore(LocalDate.now()) &&
                                !vencimiento.isAfter(fechaLimite);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BeneficiarioSeguro> obtenerBeneficiariosPorSeguro(int idSeguro) {
        return beneficiarioRepository.findBySeguroId(idSeguro);
    }

    @Override
    public BeneficiarioSeguro agregarBeneficiario(BeneficiarioSeguro beneficiario) {
        logger.info("Agregando beneficiario para seguro ID: {}", beneficiario.getSeguro().getIdSeguro());

        // Validar que no se exceda el 100%
        Double porcentajeActual = beneficiarioRepository.sumPorcentajesBySeguro(
                beneficiario.getSeguro().getIdSeguro());

        if (porcentajeActual + beneficiario.getPorcentaje() > 100) {
            throw new RuntimeException("El porcentaje total de beneficiarios no puede exceder 100%");
        }

        if (beneficiario.getEstado() == null) {
            beneficiario.setEstado("Activo");
        }

        return beneficiarioRepository.save(beneficiario);
    }

    @Override
    public BeneficiarioSeguro actualizarBeneficiario(BeneficiarioSeguro beneficiario) {
        logger.info("Actualizando beneficiario ID: {}", beneficiario.getIdBeneficiario());
        return beneficiarioRepository.save(beneficiario);
    }

    @Override
    public void eliminarBeneficiario(int idBeneficiario) {
        logger.info("Eliminando beneficiario ID: {}", idBeneficiario);
        BeneficiarioSeguro beneficiario = beneficiarioRepository.findById(idBeneficiario).orElse(null);
        if (beneficiario != null) {
            beneficiario.setEstado("Inactivo");
            beneficiarioRepository.save(beneficiario);
        }
    }

    @Override
    public boolean validarPorcentajesBeneficiarios(int idSeguro) {
        Double totalPorcentaje = beneficiarioRepository.sumPorcentajesBySeguro(idSeguro);
        return totalPorcentaje != null && totalPorcentaje == 100.0;
    }

    @Override
    public List<PagoSeguro> obtenerPagosPorSeguro(int idSeguro) {
        return pagoRepository.findBySeguroId(idSeguro);
    }

    @Override
    public List<PagoSeguro> obtenerPagosPendientes(int idUsuario) {
        return pagoRepository.findPendientesByUsuario(idUsuario);
    }

    @Override
    public PagoSeguro registrarPago(PagoSeguro pago) {
        logger.info("Registrando pago para seguro ID: {}", pago.getSeguro().getIdSeguro());
        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDateTime.now());
        }
        if (pago.getEstado() == null) {
            pago.setEstado("Pagado");
        }
        return pagoRepository.save(pago);
    }

    @Override
    public PagoSeguro actualizarPago(PagoSeguro pago) {
        logger.info("Actualizando pago ID: {}", pago.getIdPago());
        return pagoRepository.save(pago);
    }

    @Override
    public Double calcularTotalPendiente(int idUsuario) {
        return pagoRepository.sumMontosPendientesByUsuario(idUsuario);
    }

    @Override
    public List<TramiteSeguro> obtenerTramitesUsuario(int idUsuario) {
        return tramiteRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<TramiteSeguro> obtenerTramitesPendientes(int idUsuario) {
        return tramiteRepository.findPendientesByUsuario(idUsuario);
    }

    @Override
    public TramiteSeguro crearTramite(TramiteSeguro tramite) {
        logger.info("Creando trámite para usuario ID: {}", tramite.getUsuario().getIdUsuario());
        if (tramite.getFechaSolicitud() == null) {
            tramite.setFechaSolicitud(LocalDateTime.now());
        }
        if (tramite.getEstado() == null) {
            tramite.setEstado("Pendiente");
        }
        if (tramite.getPrioridad() == null) {
            tramite.setPrioridad("Media");
        }
        return tramiteRepository.save(tramite);
    }

    @Override
    public TramiteSeguro actualizarTramite(TramiteSeguro tramite) {
        logger.info("Actualizando trámite ID: {}", tramite.getIdTramite());
        return tramiteRepository.save(tramite);
    }

    @Override
    public void eliminarTramite(int idTramite) {
        logger.info("Eliminando trámite ID: {}", idTramite);
        tramiteRepository.deleteById(idTramite);
    }

    @Override
    public HashMap<String, Object> obtenerEstadisticas(int idUsuario) {
        HashMap<String, Object> stats = new HashMap<>();

        List<Seguro> seguros = obtenerSegurosUsuario(idUsuario);
        long activos = seguros.stream()
                .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                .count();

        Double primaTotal = seguros.stream()
                .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                .mapToDouble(s -> s.getPrimaMensual() != null ? s.getPrimaMensual() : 0)
                .sum();

        Double coberturaTotal = seguros.stream()
                .filter(s -> "Vigente".equals(s.getEstado()) || "Activo".equals(s.getEstado()))
                .mapToDouble(s -> s.getMontoAsegurado() != null ? s.getMontoAsegurado() : 0)
                .sum();

        stats.put("totalSeguros", seguros.size());
        stats.put("segurosActivos", activos);
        stats.put("primaMensualTotal", primaTotal);
        stats.put("coberturaTotalAsegurada", coberturaTotal);

        return stats;
    }
}