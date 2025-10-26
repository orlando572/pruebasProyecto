package com.app.financiera.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.financiera.entity.Afp;
import com.app.financiera.service.AfpService;
import com.app.financiera.util.AppSettings;

@RestController
@RequestMapping("/api/admin/afps")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class AdminAfpController {

    private static final Logger logger = LoggerFactory.getLogger(AdminAfpController.class);

    @Autowired
    private AfpService afpService;

    @GetMapping
    public ResponseEntity<?> listarAfps(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {
        try {
            logger.info("Listando AFPs - Búsqueda: {}, Estado: {}", busqueda, estado);
            List<Afp> afps;

            if (busqueda != null && !busqueda.isEmpty()) {
                afps = afpService.buscarPorNombre(busqueda);
            } else if (estado != null && !estado.isEmpty()) {
                afps = afpService.buscarPorEstado(estado);
            } else {
                afps = afpService.listarAfps();
            }

            return ResponseEntity.ok(afps);
        } catch (Exception e) {
            logger.error("Error al listar AFPs: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al listar AFPs");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAfpPorId(@PathVariable int id) {
        try {
            logger.info("Obteniendo AFP por ID: {}", id);
            Afp afp = afpService.buscarPorId(id);

            if (afp == null) {
                return ResponseEntity.status(404).body("AFP no encontrada");
            }

            return ResponseEntity.ok(afp);
        } catch (Exception e) {
            logger.error("Error al obtener AFP: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener AFP");
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            HashMap<String, Object> stats = afpService.obtenerEstadisticas();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener estadísticas");
        }
    }

    @PostMapping
    public ResponseEntity<?> crearAfp(@RequestBody Afp afp) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            logger.info("Creando nueva AFP: {}", afp.getNombre());

            // Verificar si ya existe AFP con el mismo código SBS
            Afp existente = afpService.buscarPorCodigoSbs(afp.getCodigoSbs());
            if (existente != null) {
                respuesta.put("mensaje", "Ya existe una AFP con ese código SBS");
                return ResponseEntity.status(400).body(respuesta);
            }

            Afp nuevaAfp = afpService.guardarAfp(afp);
            respuesta.put("mensaje", "AFP creada exitosamente");
            respuesta.put("data", nuevaAfp);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al crear AFP: {}", e.getMessage());
            respuesta.put("mensaje", "Error al crear AFP");
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarAfp(
            @PathVariable int id,
            @RequestBody Afp afp) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            logger.info("Actualizando AFP ID: {}", id);

            Afp existente = afpService.buscarPorId(id);
            if (existente == null) {
                respuesta.put("mensaje", "AFP no encontrada");
                return ResponseEntity.status(404).body(respuesta);
            }

            afp.setIdAfp(id);
            Afp actualizada = afpService.actualizarAfp(afp);

            respuesta.put("mensaje", "AFP actualizada exitosamente");
            respuesta.put("data", actualizada);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al actualizar AFP: {}", e.getMessage());
            respuesta.put("mensaje", "Error al actualizar AFP");
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarAfp(@PathVariable int id) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            logger.info("Eliminando AFP ID: {}", id);

            Afp existente = afpService.buscarPorId(id);
            if (existente == null) {
                respuesta.put("mensaje", "AFP no encontrada");
                return ResponseEntity.status(404).body(respuesta);
            }

            existente.setEstado("Inactivo");
            afpService.actualizarAfp(existente);

            respuesta.put("mensaje", "AFP eliminada exitosamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al eliminar AFP: {}", e.getMessage());
            respuesta.put("mensaje", "Error al eliminar AFP");
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable int id,
            @RequestBody HashMap<String, String> body) {
        HashMap<String, Object> respuesta = new HashMap<>();
        try {
            String nuevoEstado = body.get("estado");
            logger.info("Cambiando estado de AFP ID {} a {}", id, nuevoEstado);

            Afp existente = afpService.buscarPorId(id);
            if (existente == null) {
                respuesta.put("mensaje", "AFP no encontrada");
                return ResponseEntity.status(404).body(respuesta);
            }

            existente.setEstado(nuevoEstado);
            afpService.actualizarAfp(existente);

            respuesta.put("mensaje", "Estado actualizado exitosamente");
            respuesta.put("data", existente);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al cambiar estado: {}", e.getMessage());
            respuesta.put("mensaje", "Error al cambiar estado");
            return ResponseEntity.status(500).body(respuesta);
        }
    }

}