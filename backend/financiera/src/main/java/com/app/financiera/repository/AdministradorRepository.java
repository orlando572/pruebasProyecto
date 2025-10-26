package com.app.financiera.repository;

import com.app.financiera.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    // Buscar administradores activos
    @Query("SELECT a FROM Administrador a WHERE a.estado = 'Activo'")
    List<Administrador> findActivos();

    // Buscar por id_usuario
    @Query("SELECT a FROM Administrador a WHERE a.usuario.idUsuario = ?1")
    Administrador findByUsuarioId(int idUsuario);
}
