package com.app.financiera.service;

import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TipoSeguro;
import com.app.financiera.entity.CompaniaSeguro;
import com.app.financiera.repository.SeguroRepository;
import com.app.financiera.repository.TipoSeguroRepository;
import com.app.financiera.repository.CompaniaSeguroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComparadorServiceImpl implements ComparadorService {

    private static final Logger logger = LoggerFactory.getLogger(ComparadorServiceImpl.class);

    @Autowired
    private SeguroRepository seguroRepository;

    @Autowired
    private TipoSeguroRepository tipoSeguroRepository;

    @Autowired
    private CompaniaSeguroRepository companiaSeguroRepository;

    @Override
    public List<String> obtenerCategorias() {
        logger.info("Obteniendo todas las categorías de seguros");
        return tipoSeguroRepository.findAllCategorias();
    }

    @Override
    public List<TipoSeguro> obtenerTiposPorCategoria(String categoria) {
        logger.info("Obteniendo tipos de seguro para categoría: {}", categoria);
        return tipoSeguroRepository.findByCategoria(categoria);
    }

    @Override
    public List<CompaniaSeguro> obtenerCompanias() {
        logger.info("Obteniendo todas las compañías activas");
        return companiaSeguroRepository.findActivas();
    }

    @Override
    public List<Seguro> obtenerSegurosPorCategoria(String categoria) {
        logger.info("Obteniendo seguros por categoría: {}", categoria);
        return seguroRepository.findByCategoria(categoria);
    }

    @Override
    public List<Seguro> obtenerSegurosPorTipo(int idTipoSeguro) {
        logger.info("Obteniendo seguros por tipo: {}", idTipoSeguro);
        return seguroRepository.findByTipoSeguro(idTipoSeguro);
    }

    @Override
    public List<Seguro> obtenerSegurosPorCompania(int idCompania) {
        logger.info("Obteniendo seguros por compañía: {}", idCompania);
        return seguroRepository.findByCompania(idCompania);
    }

    @Override
    public Map<String, Object> compararSeguros(List<Integer> idsPlanes) {
        logger.info("Comparando {} planes de seguros", idsPlanes.size());

        Map<String, Object> comparacion = new HashMap<>();
        List<Map<String, Object>> planesComparados = new ArrayList<>();

        for (Integer idPlan : idsPlanes) {
            Optional<Seguro> seguroOpt = seguroRepository.findById(idPlan);
            if (seguroOpt.isPresent()) {
                Seguro seguro = seguroOpt.get();
                Map<String, Object> planInfo = new HashMap<>();

                planInfo.put("idSeguro", seguro.getIdSeguro());
                planInfo.put("nombrePlan", seguro.getTipoSeguro().getNombre());
                planInfo.put("compania", seguro.getCompania().getNombre());
                planInfo.put("primaMensual", seguro.getPrimaMensual());
                planInfo.put("primaAnual", seguro.getPrimaAnual());
                planInfo.put("montoAsegurado", seguro.getMontoAsegurado());
                planInfo.put("deducible", seguro.getDeducible());
                planInfo.put("coberturas", seguro.getCoberturas());
                planInfo.put("exclusiones", seguro.getExclusiones());
                planInfo.put("formaPago", seguro.getFormaPago());
                planInfo.put("numeroPoliza", seguro.getNumeroPoliza());

                planesComparados.add(planInfo);
            }
        }

        comparacion.put("planes", planesComparados);
        comparacion.put("totalComparados", planesComparados.size());

        // Calcular estadísticas de comparación
        if (!planesComparados.isEmpty()) {
            DoubleSummaryStatistics statsPrecios = planesComparados.stream()
                    .mapToDouble(p -> (Double) p.get("primaMensual"))
                    .summaryStatistics();

            DoubleSummaryStatistics statsCoberturas = planesComparados.stream()
                    .mapToDouble(p -> (Double) p.get("montoAsegurado"))
                    .summaryStatistics();

            comparacion.put("precioMin", statsPrecios.getMin());
            comparacion.put("precioMax", statsPrecios.getMax());
            comparacion.put("precioPromedio", statsPrecios.getAverage());
            comparacion.put("coberturaMin", statsCoberturas.getMin());
            comparacion.put("coberturaMax", statsCoberturas.getMax());
            comparacion.put("coberturaPromedio", statsCoberturas.getAverage());
        }

        return comparacion;
    }

    @Override
    public List<Seguro> filtrarSeguros(String categoria, Double primaMin, Double primaMax,
                                       Double coberturaMin, Double coberturaMax) {
        logger.info("Filtrando seguros - Categoría: {}, Prima: {}-{}, Cobertura: {}-{}",
                categoria, primaMin, primaMax, coberturaMin, coberturaMax);

        List<Seguro> seguros = seguroRepository.findByCategoria(categoria);

        return seguros.stream()
                .filter(s -> {
                    boolean cumplePrima = true;
                    boolean cumpleCobertura = true;

                    if (primaMin != null && primaMax != null) {
                        cumplePrima = s.getPrimaMensual() >= primaMin && s.getPrimaMensual() <= primaMax;
                    }

                    if (coberturaMin != null && coberturaMax != null) {
                        cumpleCobertura = s.getMontoAsegurado() >= coberturaMin &&
                                s.getMontoAsegurado() <= coberturaMax;
                    }

                    return cumplePrima && cumpleCobertura;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> obtenerResumenPlan(int idSeguro) {
        logger.info("Obteniendo resumen de plan: {}", idSeguro);

        Map<String, Object> resumen = new HashMap<>();
        Optional<Seguro> seguroOpt = seguroRepository.findById(idSeguro);

        if (seguroOpt.isPresent()) {
            Seguro seguro = seguroOpt.get();

            resumen.put("nombrePlan", seguro.getTipoSeguro().getNombre());
            resumen.put("descripcion", seguro.getTipoSeguro().getDescripcion());
            resumen.put("categoria", seguro.getTipoSeguro().getCategoria());
            resumen.put("compania", seguro.getCompania().getNombre());
            resumen.put("calificacion", seguro.getCompania().getCalificacionRiesgo());
            resumen.put("primaMensual", seguro.getPrimaMensual());
            resumen.put("primaAnual", seguro.getPrimaAnual());
            resumen.put("montoAsegurado", seguro.getMontoAsegurado());
            resumen.put("deducible", seguro.getDeducible());
            resumen.put("coberturas", seguro.getCoberturas());
            resumen.put("exclusiones", seguro.getExclusiones());
            resumen.put("contactoCompania", Map.of(
                    "telefono", seguro.getCompania().getTelefono(),
                    "email", seguro.getCompania().getEmail(),
                    "web", seguro.getCompania().getSitioWeb()
            ));
            resumen.put("exitoso", true);
        } else {
            resumen.put("exitoso", false);
            resumen.put("mensaje", "Plan no encontrado");
        }

        return resumen;
    }

    @Override
    public Map<String, Object> obtenerEstadisticasCategoria(String categoria) {
        logger.info("Obteniendo estadísticas para categoría: {}", categoria);

        Map<String, Object> estadisticas = new HashMap<>();
        List<Seguro> seguros = seguroRepository.findByCategoria(categoria);

        if (!seguros.isEmpty()) {
            DoubleSummaryStatistics statsPrecios = seguros.stream()
                    .mapToDouble(Seguro::getPrimaMensual)
                    .summaryStatistics();

            DoubleSummaryStatistics statsCoberturas = seguros.stream()
                    .mapToDouble(Seguro::getMontoAsegurado)
                    .summaryStatistics();

            // Agrupar por compañía
            Map<String, Long> planesPorCompania = seguros.stream()
                    .collect(Collectors.groupingBy(
                            s -> s.getCompania().getNombre(),
                            Collectors.counting()
                    ));

            estadisticas.put("totalPlanes", seguros.size());
            estadisticas.put("primaMinima", statsPrecios.getMin());
            estadisticas.put("primaMaxima", statsPrecios.getMax());
            estadisticas.put("primaPromedio", statsPrecios.getAverage());
            estadisticas.put("coberturaMinima", statsCoberturas.getMin());
            estadisticas.put("coberturaMaxima", statsCoberturas.getMax());
            estadisticas.put("coberturaPromedio", statsCoberturas.getAverage());
            estadisticas.put("planesPorCompania", planesPorCompania);
            estadisticas.put("companias", planesPorCompania.size());
        } else {
            estadisticas.put("totalPlanes", 0);
            estadisticas.put("mensaje", "No hay planes disponibles en esta categoría");
        }

        return estadisticas;
    }
}