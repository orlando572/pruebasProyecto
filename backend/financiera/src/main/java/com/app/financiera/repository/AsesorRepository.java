package com.app.financiera.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.financiera.entity.Asesor;

public interface AsesorRepository extends JpaRepository<Asesor, Integer> {

    // Buscar por código de asesor
    Optional<Asesor> findByCodigoAsesor(String codigoAsesor);

    // Buscar asesores por especialidad
    @Query("SELECT a FROM Asesor a WHERE a.especialidad = ?1 AND a.estado = 'Activo'")
    List<Asesor> findByEspecialidad(String especialidad);

    // Buscar asesores disponibles
    @Query("SELECT a FROM Asesor a WHERE a.estado IN ('Activo', 'Disponible') ORDER BY a.calificacionPromedio DESC")
    List<Asesor> findAsesoresDisponibles();

    // Buscar asesores por estado
    @Query("SELECT a FROM Asesor a WHERE a.estado = ?1")
    List<Asesor> findByEstado(String estado);

    // Buscar mejor asesor por especialidad
    @Query("SELECT a FROM Asesor a WHERE a.especialidad = ?1 AND a.estado IN ('Activo', 'Disponible') ORDER BY a.calificacionPromedio DESC LIMIT 1")
    Optional<Asesor> findMejorAsesorPorEspecialidad(String especialidad);

    // Buscar por usuario
    @Query("SELECT a FROM Asesor a WHERE a.usuario.idUsuario = ?1")
    Optional<Asesor> findByUsuarioId(int idUsuario);

    // Contar asesores activos
    @Query("SELECT COUNT(a) FROM Asesor a WHERE a.estado = 'Activo'")
    long countAsesoresActivos();
}
