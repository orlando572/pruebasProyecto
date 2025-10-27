package com.app.financiera.repository;

import com.app.financiera.entity.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface SeguroRepository extends JpaRepository<Seguro, Integer> {

    // Obtener todos los seguros activos
    @Query("SELECT s FROM Seguro s WHERE s.estado = 'Activo'")
    List<Seguro> findAllActivos();

    // Obtener seguros por categoría
    @Query("SELECT s FROM Seguro s WHERE s.tipoSeguro.categoria = ?1 AND s.estado = 'Activo'")
    List<Seguro> findByCategoria(String categoria);

    // Obtener seguros por tipo de seguro específico
    @Query("SELECT s FROM Seguro s WHERE s.tipoSeguro.idTipoSeguro = ?1 AND s.estado = 'Activo'")
    List<Seguro> findByTipoSeguro(int idTipoSeguro);

    // Obtener seguros por compañía
    @Query("SELECT s FROM Seguro s WHERE s.compania.idCompania = ?1 AND s.estado = 'Activo'")
    List<Seguro> findByCompania(int idCompania);

    // Obtener seguros de un usuario
    @Query("SELECT s FROM Seguro s WHERE s.usuario.idUsuario = ?1 AND (s.estado = 'Activo' OR s.estado = 'Vigente')")
    List<Seguro> findByUsuario(int idUsuario);

    // Buscar seguros por rango de prima mensual
    @Query("SELECT s FROM Seguro s WHERE s.primaMensual BETWEEN ?1 AND ?2 AND s.estado = 'Activo'")
    List<Seguro> findByRangoPrimaMensual(Double min, Double max);

    // Buscar seguros por rango de cobertura
    @Query("SELECT s FROM Seguro s WHERE s.montoAsegurado BETWEEN ?1 AND ?2 AND s.estado = 'Activo'")
    List<Seguro> findByRangoCobertura(Double min, Double max);

    // Obtener seguros ordenados por prima mensual
    @Query("SELECT s FROM Seguro s WHERE s.tipoSeguro.categoria = ?1 AND s.estado = 'Activo' ORDER BY s.primaMensual ASC")
    List<Seguro> findByCategoriaOrderByPrima(String categoria);
}