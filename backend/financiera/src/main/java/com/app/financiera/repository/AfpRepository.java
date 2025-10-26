package com.app.financiera.repository;

import com.app.financiera.entity.Afp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AfpRepository extends JpaRepository<Afp, Integer> {

    // Buscar por nombre exacto
    Afp findByNombre(String nombre);

    // Buscar por nombre que contenga
    @Query("SELECT a FROM Afp a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Afp> findByNombreContaining(String busqueda);

    // Buscar por código SBS
    Afp findByCodigoSbs(String codigoSbs);

    // Buscar por estado
    List<Afp> findByEstado(String estado);

    // Buscar AFPs activas
    @Query("SELECT a FROM Afp a WHERE a.estado = 'Activo'")
    List<Afp> findActivas();
}