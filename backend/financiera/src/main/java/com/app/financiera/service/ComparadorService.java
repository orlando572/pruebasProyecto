package com.app.financiera.service;

import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TipoSeguro;
import com.app.financiera.entity.CompaniaSeguro;
import java.util.List;
import java.util.Map;

public interface ComparadorService {

    // Obtener todas las categorías de seguros
    List<String> obtenerCategorias();

    // Obtener tipos de seguro por categoría
    List<TipoSeguro> obtenerTiposPorCategoria(String categoria);

    // Obtener todas las compañías activas
    List<CompaniaSeguro> obtenerCompanias();

    // Obtener seguros por categoría
    List<Seguro> obtenerSegurosPorCategoria(String categoria);

    // Obtener seguros por tipo específico
    List<Seguro> obtenerSegurosPorTipo(int idTipoSeguro);

    // Obtener seguros por compañía
    List<Seguro> obtenerSegurosPorCompania(int idCompania);

    // Comparar seguros seleccionados
    Map<String, Object> compararSeguros(List<Integer> idsPlanes);

    // Filtrar seguros por criterios
    List<Seguro> filtrarSeguros(String categoria, Double primaMin, Double primaMax,
                                Double coberturaMin, Double coberturaMax);

    // Obtener resumen de plan
    Map<String, Object> obtenerResumenPlan(int idSeguro);

    // Obtener estadísticas por categoría
    Map<String, Object> obtenerEstadisticasCategoria(String categoria);
}