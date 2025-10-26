package com.app.financiera.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.financiera.entity.HistorialRentabilidad;

public interface HistorialRentabilidadRepository extends JpaRepository<HistorialRentabilidad, Integer> {

    // Buscar por AFP y tipo de fondo
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.afp.idAfp = ?1 AND h.tipoFondo.idTipoFondo = ?2 ORDER BY h.periodo DESC")
    List<HistorialRentabilidad> findByAfpAndTipoFondo(int idAfp, int idTipoFondo);

    // Buscar por AFP
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.afp.idAfp = ?1 ORDER BY h.periodo DESC")
    List<HistorialRentabilidad> findByAfpId(int idAfp);

    // Buscar por periodo
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.periodo = ?1")
    List<HistorialRentabilidad> findByPeriodo(String periodo);

    // Buscar último registro por AFP y fondo
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.afp.idAfp = ?1 AND h.tipoFondo.idTipoFondo = ?2 ORDER BY h.periodo DESC LIMIT 1")
    Optional<HistorialRentabilidad> findUltimoRegistro(int idAfp, int idTipoFondo);

    // Buscar últimos N periodos
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.afp.idAfp = ?1 AND h.tipoFondo.idTipoFondo = ?2 ORDER BY h.periodo DESC LIMIT ?3")
    List<HistorialRentabilidad> findUltimosPeriodos(int idAfp, int idTipoFondo, int cantidad);

    // Calcular rentabilidad promedio
    @Query("SELECT AVG(h.rentabilidadReal) FROM HistorialRentabilidad h WHERE h.afp.idAfp = ?1 AND h.tipoFondo.idTipoFondo = ?2")
    Double calcularRentabilidadPromedio(int idAfp, int idTipoFondo);

    // Buscar mejor rentabilidad por tipo de fondo
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.tipoFondo.idTipoFondo = ?1 AND h.periodo = ?2 ORDER BY h.rentabilidadReal DESC LIMIT 1")
    Optional<HistorialRentabilidad> findMejorRentabilidadPorFondo(int idTipoFondo, String periodo);

    // Comparar AFPs en un periodo
    @Query("SELECT h FROM HistorialRentabilidad h WHERE h.periodo = ?1 AND h.tipoFondo.idTipoFondo = ?2 ORDER BY h.rentabilidadReal DESC")
    List<HistorialRentabilidad> compararAfpsPorPeriodo(String periodo, int idTipoFondo);
}
