package com.app.financiera.repository;

import com.app.financiera.entity.HistorialConsultas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface HistorialConsultasRepository extends JpaRepository<HistorialConsultas, Integer> {

    // Obtener historial por usuario
    @Query("SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 ORDER BY h.fecha DESC")
    List<HistorialConsultas> findByUsuarioId(int idUsuario);

    // Obtener historial por tipo
    @Query("SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 AND h.tipoConsulta = ?2 ORDER BY h.fecha DESC")
    List<HistorialConsultas> findByUsuarioAndTipo(int idUsuario, String tipoConsulta);

    // Obtener historial exitoso
    @Query("SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 AND h.resultado = 'Exitoso' ORDER BY h.fecha DESC")
    List<HistorialConsultas> findExitosos(int idUsuario);

    // Obtener historial con errores
    @Query("SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 AND h.resultado = 'Error' ORDER BY h.fecha DESC")
    List<HistorialConsultas> findConErrores(int idUsuario);

    // Obtener últimas N consultas
    @Query(value = "SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 ORDER BY h.fecha DESC LIMIT ?2", nativeQuery = false)
    List<HistorialConsultas> findUltimas(int idUsuario, int limite);

    // Obtener por rango de fechas
    @Query("SELECT h FROM HistorialConsultas h WHERE h.usuario.idUsuario = ?1 AND h.fecha BETWEEN ?2 AND ?3 ORDER BY h.fecha DESC")
    List<HistorialConsultas> findByFechaRango(int idUsuario, LocalDateTime inicio, LocalDateTime fin);
}