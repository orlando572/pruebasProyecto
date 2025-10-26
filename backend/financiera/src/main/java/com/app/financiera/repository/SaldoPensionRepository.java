package com.app.financiera.repository;

import com.app.financiera.entity.SaldoPension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SaldoPensionRepository extends JpaRepository<SaldoPension, Integer> {

    // Obtener saldos por usuario
    @Query("SELECT s FROM SaldoPension s WHERE s.usuario.idUsuario = ?1 AND s.estado = 'Activo'")
    List<SaldoPension> findByUsuarioId(int idUsuario);

    // Obtener saldo por usuario y AFP
    @Query("SELECT s FROM SaldoPension s WHERE s.usuario.idUsuario = ?1 AND s.afp.idAfp = ?2")
    Optional<SaldoPension> findByUsuarioAndAfp(int idUsuario, int idAfp);

    // Obtener saldo de ONP (sin AFP)
    @Query("SELECT s FROM SaldoPension s WHERE s.usuario.idUsuario = ?1 AND s.afp IS NULL")
    Optional<SaldoPension> findONPSaldo(int idUsuario);

    // Suma total de saldos por usuario
    @Query("SELECT COALESCE(SUM(s.saldoTotal), 0) FROM SaldoPension s WHERE s.usuario.idUsuario = ?1 AND s.estado = 'Activo'")
    Double sumSaldosUsuario(int idUsuario);

    // Suma de saldos disponibles por usuario
    @Query("SELECT COALESCE(SUM(s.saldoDisponible), 0) FROM SaldoPension s WHERE s.usuario.idUsuario = ?1 AND s.estado = 'Activo'")
    Double sumSaldosDisponibles(int idUsuario);
}