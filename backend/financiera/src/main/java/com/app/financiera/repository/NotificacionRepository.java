package com.app.financiera.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.financiera.entity.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Buscar notificaciones por usuario
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = ?1 ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findByUsuarioIdOrderByFechaEnvioDesc(int idUsuario);

    // Buscar notificaciones no leídas por usuario
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = ?1 AND n.fechaLectura IS NULL ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findNotificacionesNoLeidas(int idUsuario);

    // Buscar notificaciones por estado
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = ?1 AND n.estado = ?2 ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findByUsuarioAndEstado(int idUsuario, String estado);

    // Buscar notificaciones por tipo
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = ?1 AND n.tipoNotificacion = ?2 ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findByUsuarioAndTipo(int idUsuario, String tipo);

    // Buscar notificaciones pendientes de envío
    @Query("SELECT n FROM Notificacion n WHERE n.estado = 'Pendiente' AND n.fechaEnvio <= ?1")
    List<Notificacion> findNotificacionesPendientes(LocalDateTime fecha);

    // Contar notificaciones no leídas
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.idUsuario = ?1 AND n.fechaLectura IS NULL")
    long countNotificacionesNoLeidas(int idUsuario);

    // Buscar notificaciones por prioridad
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = ?1 AND n.prioridad = ?2 ORDER BY n.fechaEnvio DESC")
    List<Notificacion> findByUsuarioAndPrioridad(int idUsuario, String prioridad);
}
