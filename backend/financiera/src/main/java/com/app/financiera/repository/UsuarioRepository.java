package com.app.financiera.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.app.financiera.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("select u from Usuario u where u.idUsuario = ?1")
    List<Usuario> listaPorIdIgual(int id);

    Usuario findByCorreo(String correo);

    // Buscar por DNI (para login)
    Usuario findByDni(String dni);

    // Buscar por ID
    List<Usuario> findByIdUsuario(int idUsuario);

    // Buscar por nombre o DNI (para admin)
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR u.dni LIKE CONCAT('%', ?1, '%')")
    List<Usuario> findByNombreOrDniContaining(String busqueda);

    // Buscar por estado
    List<Usuario> findByEstado(String estado);

    // Buscar por rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.idRol = ?1")
    List<Usuario> findByRolId(int idRol);
}