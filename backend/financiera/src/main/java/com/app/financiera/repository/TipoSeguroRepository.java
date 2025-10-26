package com.app.financiera.repository;

import com.app.financiera.entity.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface TipoSeguroRepository extends JpaRepository<TipoSeguro, Integer> {

    // Obtener tipos de seguro activos
    @Query("SELECT t FROM TipoSeguro t WHERE t.estado = 'Activo'")
    List<TipoSeguro> findActivos();

    // Obtener tipos de seguro por categoría
    @Query("SELECT t FROM TipoSeguro t WHERE t.categoria = ?1 AND t.estado = 'Activo'")
    List<TipoSeguro> findByCategoria(String categoria);

    // Obtener todas las categorías disponibles
    @Query("SELECT DISTINCT t.categoria FROM TipoSeguro t WHERE t.estado = 'Activo'")
    List<String> findAllCategorias();
}