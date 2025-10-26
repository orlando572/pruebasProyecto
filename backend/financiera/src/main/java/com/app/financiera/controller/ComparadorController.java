package com.app.financiera.controller;

import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TipoSeguro;
import com.app.financiera.entity.CompaniaSeguro;
import com.app.financiera.service.ComparadorService;
import com.app.financiera.util.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comparador")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class ComparadorController {

    private static final Logger logger = LoggerFactory.getLogger(ComparadorController.class);

    @Autowired
    private ComparadorService comparadorService;

    // Obtener todas las categorías de seguros
    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        logger.info("Solicitud de categorías de seguros");
        try {
            List<String> categorias = comparadorService.obtenerCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            logger.error("Error al obtener categorías: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener categorías");
        }
    }

    // Obtener tipos de seguro por categoría
    @GetMapping("/tipos/{categoria}")
    public ResponseEntity<?> obtenerTiposPorCategoria(@PathVariable String categoria) {
        logger.info("Solicitud de tipos de seguro para categoría: {}", categoria);
        try {
            List<TipoSeguro> tipos = comparadorService.obtenerTiposPorCategoria(categoria);
            return ResponseEntity.ok(tipos);
        } catch (Exception e) {
            logger.error("Error al obtener tipos: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener tipos de seguro");
        }
    }

    // Obtener todas las compañías
    @GetMapping("/companias")
    public ResponseEntity<?> obtenerCompanias() {
        logger.info("Solicitud de compañías de seguros");
        try {
            List<CompaniaSeguro> companias = comparadorService.obtenerCompanias();
            return ResponseEntity.ok(companias);
        } catch (Exception e) {
            logger.error("Error al obtener compañías: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener compañías");
        }
    }

    // Obtener seguros por categoría
    @GetMapping("/seguros/categoria/{categoria}")
    public ResponseEntity<?> obtenerSegurosPorCategoria(@PathVariable String categoria) {
        logger.info("Solicitud de seguros por categoría: {}", categoria);
        try {
            List<Seguro> seguros = comparadorService.obtenerSegurosPorCategoria(categoria);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            logger.error("Error al obtener seguros: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener seguros");
        }
    }

    // Obtener seguros por tipo específico
    @GetMapping("/seguros/tipo/{idTipo}")
    public ResponseEntity<?> obtenerSegurosPorTipo(@PathVariable int idTipo) {
        logger.info("Solicitud de seguros por tipo: {}", idTipo);
        try {
            List<Seguro> seguros = comparadorService.obtenerSegurosPorTipo(idTipo);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            logger.error("Error al obtener seguros: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener seguros");
        }
    }

    // Obtener seguros por compañía
    @GetMapping("/seguros/compania/{idCompania}")
    public ResponseEntity<?> obtenerSegurosPorCompania(@PathVariable int idCompania) {
        logger.info("Solicitud de seguros por compañía: {}", idCompania);
        try {
            List<Seguro> seguros = comparadorService.obtenerSegurosPorCompania(idCompania);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            logger.error("Error al obtener seguros: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener seguros");
        }
    }

    // Comparar planes seleccionados
    @PostMapping("/comparar")
    public ResponseEntity<?> compararPlanes(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> idsPlanes = request.get("idsPlanes");
        logger.info("Solicitud de comparación de {} planes", idsPlanes.size());

        try {
            if (idsPlanes == null || idsPlanes.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe seleccionar al menos un plan");
            }

            if (idsPlanes.size() > 5) {
                return ResponseEntity.badRequest().body("No puede comparar más de 5 planes");
            }

            Map<String, Object> comparacion = comparadorService.compararSeguros(idsPlanes);
            return ResponseEntity.ok(comparacion);
        } catch (Exception e) {
            logger.error("Error al comparar planes: {}", e.getMessage());
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al comparar planes");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Filtrar seguros por criterios
    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarSeguros(
            @RequestParam String categoria,
            @RequestParam(required = false) Double primaMin,
            @RequestParam(required = false) Double primaMax,
            @RequestParam(required = false) Double coberturaMin,
            @RequestParam(required = false) Double coberturaMax) {

        logger.info("Filtrando seguros - Categoría: {}", categoria);
        try {
            List<Seguro> seguros = comparadorService.filtrarSeguros(
                    categoria, primaMin, primaMax, coberturaMin, coberturaMax
            );
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            logger.error("Error al filtrar seguros: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al filtrar seguros");
        }
    }

    @GetMapping("/plan/{idSeguro}")
    public ResponseEntity<?> obtenerResumenPlan(@PathVariable int idSeguro) {
        logger.info("Solicitud de resumen de plan: {}", idSeguro);
        try {
            Map<String, Object> resumen = comparadorService.obtenerResumenPlan(idSeguro);

            if (resumen.get("exitoso") != null && !(Boolean) resumen.get("exitoso")) {
                return ResponseEntity.status(404).body(resumen);
            }

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error al obtener resumen: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener resumen del plan");
        }
    }

    @GetMapping("/estadisticas/{categoria}")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable String categoria) {
        logger.info("Solicitud de estadísticas para categoría: {}", categoria);
        try {
            Map<String, Object> estadisticas = comparadorService.obtenerEstadisticasCategoria(categoria);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener estadísticas");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        HashMap<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("servicio", "Comparador de Seguros");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
}