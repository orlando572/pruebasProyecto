package com.app.financiera.repository;

import com.app.financiera.entity.TramiteSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TramiteSeguroRepository extends JpaRepository<TramiteSeguro, Integer> {

    // Obtener trámites por usuario
    @Query("SELECT t FROM TramiteSeguro t WHERE t.usuario.idUsuario = ?1 ORDER BY t.fechaSolicitud DESC")
    List<TramiteSeguro> findByUsuarioId(int idUsuario);

    // Obtener trámites por estado
    @Query("SELECT t FROM TramiteSeguro t WHERE t.usuario.idUsuario = ?1 AND t.estado = ?2 ORDER BY t.fechaSolicitud DESC")
    List<TramiteSeguro> findByUsuarioAndEstado(int idUsuario, String estado);

    // Obtener trámites pendientes o en proceso
    @Query("SELECT t FROM TramiteSeguro t WHERE t.usuario.idUsuario = ?1 AND t.estado IN ('Pendiente', 'En proceso') ORDER BY t.prioridad, t.fechaSolicitud")
    List<TramiteSeguro> findPendientesByUsuario(int idUsuario);

    // Obtener trámites por tipo
    @Query("SELECT t FROM TramiteSeguro t WHERE t.usuario.idUsuario = ?1 AND t.tipoTramite = ?2 ORDER BY t.fechaSolicitud DESC")
    List<TramiteSeguro> findByUsuarioAndTipo(int idUsuario, String tipoTramite);

    // Obtener trámites por seguro
    @Query("SELECT t FROM TramiteSeguro t WHERE t.seguro.idSeguro = ?1 ORDER BY t.fechaSolicitud DESC")
    List<TramiteSeguro> findBySeguroId(int idSeguro);

    // Contar trámites pendientes por usuario
    @Query("SELECT COUNT(t) FROM TramiteSeguro t WHERE t.usuario.idUsuario = ?1 AND t.estado IN ('Pendiente', 'En proceso')")
    long countPendientesByUsuario(int idUsuario);
}