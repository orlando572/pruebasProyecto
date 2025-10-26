package com.app.financiera.repository;

import com.app.financiera.entity.PagoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PagoSeguroRepository extends JpaRepository<PagoSeguro, Integer> {

    // Obtener pagos por seguro
    @Query("SELECT p FROM PagoSeguro p WHERE p.seguro.idSeguro = ?1 ORDER BY p.numeroCuota")
    List<PagoSeguro> findBySeguroId(int idSeguro);

    // Obtener pagos pendientes por usuario
    @Query("SELECT p FROM PagoSeguro p WHERE p.seguro.usuario.idUsuario = ?1 AND p.estado = 'Pendiente' ORDER BY p.numeroCuota")
    List<PagoSeguro> findPendientesByUsuario(int idUsuario);

    // Obtener último pago de un seguro
    @Query("SELECT p FROM PagoSeguro p WHERE p.seguro.idSeguro = ?1 ORDER BY p.numeroCuota DESC LIMIT 1")
    PagoSeguro findUltimoPagoBySeguro(int idSeguro);

    // Contar pagos pendientes por usuario
    @Query("SELECT COUNT(p) FROM PagoSeguro p WHERE p.seguro.usuario.idUsuario = ?1 AND p.estado = 'Pendiente'")
    long countPendientesByUsuario(int idUsuario);

    // Suma de montos pendientes por usuario
    @Query("SELECT COALESCE(SUM(p.montoPagado), 0) FROM PagoSeguro p WHERE p.seguro.usuario.idUsuario = ?1 AND p.estado = 'Pendiente'")
    Double sumMontosPendientesByUsuario(int idUsuario);
}