package com.app.financiera.repository;

import com.app.financiera.entity.BeneficiarioSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BeneficiarioSeguroRepository extends JpaRepository<BeneficiarioSeguro, Integer> {

    // Obtener beneficiarios por seguro
    @Query("SELECT b FROM BeneficiarioSeguro b WHERE b.seguro.idSeguro = ?1 AND b.estado = 'Activo'")
    List<BeneficiarioSeguro> findBySeguroId(int idSeguro);

    // Verificar porcentaje total asignado
    @Query("SELECT COALESCE(SUM(b.porcentaje), 0) FROM BeneficiarioSeguro b WHERE b.seguro.idSeguro = ?1 AND b.estado = 'Activo'")
    Double sumPorcentajesBySeguro(int idSeguro);

    // Contar beneficiarios activos por seguro
    @Query("SELECT COUNT(b) FROM BeneficiarioSeguro b WHERE b.seguro.idSeguro = ?1 AND b.estado = 'Activo'")
    long countBySeguroId(int idSeguro);
}