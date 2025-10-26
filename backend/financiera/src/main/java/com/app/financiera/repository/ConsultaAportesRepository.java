package com.app.financiera.repository;

import com.app.financiera.entity.ConsultaAportes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaAportesRepository extends JpaRepository<ConsultaAportes, Integer> {

    // Obtener consultas por usuario
    @Query("SELECT c FROM ConsultaAportes c WHERE c.usuario.idUsuario = ?1 ORDER BY c.fechaConsulta DESC")
    List<ConsultaAportes> findByUsuarioId(int idUsuario);

    // Obtener consultas por usuario y tipo
    @Query("SELECT c FROM ConsultaAportes c WHERE c.usuario.idUsuario = ?1 AND c.tipoConsulta = ?2 ORDER BY c.fechaConsulta DESC")
    List<ConsultaAportes> findByUsuarioAndTipo(int idUsuario, String tipoConsulta);

    // Obtener consultas exitosas
    @Query("SELECT c FROM ConsultaAportes c WHERE c.usuario.idUsuario = ?1 AND c.exitoso = true ORDER BY c.fechaConsulta DESC")
    List<ConsultaAportes> findExitosas(int idUsuario);

    // Obtener consultas en rango de fechas
    @Query("SELECT c FROM ConsultaAportes c WHERE c.usuario.idUsuario = ?1 AND c.fechaConsulta BETWEEN ?2 AND ?3 ORDER BY c.fechaConsulta DESC")
    List<ConsultaAportes> findByFechaRango(int idUsuario, LocalDateTime inicio, LocalDateTime fin);

    // Contar consultas por tipo
    @Query("SELECT COUNT(c) FROM ConsultaAportes c WHERE c.usuario.idUsuario = ?1 AND c.tipoConsulta = ?2")
    long countByTipo(int idUsuario, String tipoConsulta);
}