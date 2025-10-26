package com.app.financiera.service;

import java.util.HashMap;
import java.util.List;
import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TramiteSeguro;
import com.app.financiera.entity.BeneficiarioSeguro;
import com.app.financiera.entity.PagoSeguro;

public interface SegurosService {

    // RESUMEN ADMINISTRATIVO
    HashMap<String, Object> obtenerResumenAdministrativo(int idUsuario);

    // SEGUROS (PÓLIZAS)
    List<Seguro> obtenerSegurosUsuario(int idUsuario);
    List<Seguro> obtenerSegurosActivos(int idUsuario);
    Seguro obtenerSeguroPorId(int idSeguro);
    Seguro crearSeguro(Seguro seguro);
    Seguro actualizarSeguro(Seguro seguro);
    void eliminarSeguro(int idSeguro);
    List<Seguro> obtenerSegurosProximosVencer(int idUsuario, int dias);

    // BENEFICIARIOS
    List<BeneficiarioSeguro> obtenerBeneficiariosPorSeguro(int idSeguro);
    BeneficiarioSeguro agregarBeneficiario(BeneficiarioSeguro beneficiario);
    BeneficiarioSeguro actualizarBeneficiario(BeneficiarioSeguro beneficiario);
    void eliminarBeneficiario(int idBeneficiario);
    boolean validarPorcentajesBeneficiarios(int idSeguro);

    // PAGOS
    List<PagoSeguro> obtenerPagosPorSeguro(int idSeguro);
    List<PagoSeguro> obtenerPagosPendientes(int idUsuario);
    PagoSeguro registrarPago(PagoSeguro pago);
    PagoSeguro actualizarPago(PagoSeguro pago);
    Double calcularTotalPendiente(int idUsuario);

    // TRÁMITES Y RECLAMOS
    List<TramiteSeguro> obtenerTramitesUsuario(int idUsuario);
    List<TramiteSeguro> obtenerTramitesPendientes(int idUsuario);
    TramiteSeguro crearTramite(TramiteSeguro tramite);
    TramiteSeguro actualizarTramite(TramiteSeguro tramite);
    void eliminarTramite(int idTramite);

    // ESTADÍSTICAS
    HashMap<String, Object> obtenerEstadisticas(int idUsuario);
}