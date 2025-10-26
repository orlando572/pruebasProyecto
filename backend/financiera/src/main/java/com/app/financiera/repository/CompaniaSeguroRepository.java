package com.app.financiera.repository;

import com.app.financiera.entity.CompaniaSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CompaniaSeguroRepository extends JpaRepository<CompaniaSeguro, Integer> {

    // Obtener compañías activas
    @Query("SELECT c FROM CompaniaSeguro c WHERE c.estado = 'Activo'")
    List<CompaniaSeguro> findActivas();

    // Buscar por nombre
    @Query("SELECT c FROM CompaniaSeguro c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) AND c.estado = 'Activo'")
    List<CompaniaSeguro> findByNombre(String nombre);
}