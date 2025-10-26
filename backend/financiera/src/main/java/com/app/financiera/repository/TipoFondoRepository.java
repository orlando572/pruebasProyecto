package com.app.financiera.repository;

import com.app.financiera.entity.TipoFondo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TipoFondoRepository extends JpaRepository<TipoFondo, Integer> {

    // Obtener fondos activos
    @Query("SELECT t FROM TipoFondo t WHERE t.estado = 'Activo'")
    List<TipoFondo> findActivos();

    // Obtener fondos por categoría
    @Query("SELECT t FROM TipoFondo t WHERE t.categoria = ?1 AND t.estado = 'Activo'")
    List<TipoFondo> findByCategoria(String categoria);

    // Buscar por nombre
    @Query("SELECT t FROM TipoFondo t WHERE LOWER(t.nombreTipo) LIKE %?1% AND t.estado = 'Activo'")
    List<TipoFondo> findByNombre(String nombre);
}