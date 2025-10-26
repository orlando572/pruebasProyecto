package com.app.financiera.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.financiera.entity.AsesoramientoFinanciero;

public interface AsesoramientoFinancieroRepository extends JpaRepository<AsesoramientoFinanciero, Integer> {

    // Buscar asesoramientos por usuario
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.usuario.idUsuario = ?1 ORDER BY a.fechaAsesoramiento DESC")
    List<AsesoramientoFinanciero> findByUsuarioId(int idUsuario);

    // Buscar asesoramientos por asesor
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.asesor.idAsesor = ?1 ORDER BY a.fechaAsesoramiento DESC")
    List<AsesoramientoFinanciero> findByAsesorId(int idAsesor);

    // Buscar asesoramientos por estado
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.estado = ?1 ORDER BY a.fechaAsesoramiento DESC")
    List<AsesoramientoFinanciero> findByEstado(String estado);

    // Buscar asesoramientos programados
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.estado = 'Programado' AND a.fechaAsesoramiento BETWEEN ?1 AND ?2")
    List<AsesoramientoFinanciero> findAsesoramientosProgramados(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Buscar asesoramientos que requieren seguimiento
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.seguimientoRequerido = true AND a.estado = 'Completado' ORDER BY a.fechaSeguimiento ASC")
    List<AsesoramientoFinanciero> findAsesoramientosConSeguimiento();

    // Buscar por tipo de asesoramiento
    @Query("SELECT a FROM AsesoramientoFinanciero a WHERE a.usuario.idUsuario = ?1 AND a.tipoAsesoramiento = ?2 ORDER BY a.fechaAsesoramiento DESC")
    List<AsesoramientoFinanciero> findByUsuarioAndTipo(int idUsuario, String tipo);

    // Contar asesoramientos por asesor
    @Query("SELECT COUNT(a) FROM AsesoramientoFinanciero a WHERE a.asesor.idAsesor = ?1 AND a.estado = 'Completado'")
    long countAsesoramientosCompletados(int idAsesor);

    // Calcular satisfacción promedio de asesor
    @Query("SELECT AVG(a.satisfaccionCliente) FROM AsesoramientoFinanciero a WHERE a.asesor.idAsesor = ?1 AND a.satisfaccionCliente IS NOT NULL")
    Double calcularSatisfaccionPromedio(int idAsesor);
}
