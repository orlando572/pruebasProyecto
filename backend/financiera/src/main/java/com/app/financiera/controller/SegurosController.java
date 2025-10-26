package com.app.financiera.controller;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.financiera.entity.Seguro;
import com.app.financiera.entity.TramiteSeguro;
import com.app.financiera.entity.BeneficiarioSeguro;
import com.app.financiera.entity.PagoSeguro;
import com.app.financiera.service.SegurosService;
import com.app.financiera.util.AppSettings;

@RestController
@RequestMapping("/api/seguros")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class SegurosController {

    private static final Logger logger = LoggerFactory.getLogger(SegurosController.class);

    @Autowired
    private SegurosService segurosService;

    @GetMapping("/resumen/{idUsuario}")
    public ResponseEntity<?> obtenerResumen(@PathVariable int idUsuario) {
        logger.info("Solicitud de resumen de seguros para usuario: {}", idUsuario);
        try {
            HashMap<String, Object> resumen = segurosService.obtenerResumenAdministrativo(idUsuario);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error al obtener resumen: {}", e.getMessage());
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al obtener resumen");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/polizas/{idUsuario}")
    public ResponseEntity<?> obtenerPolizas(@PathVariable int idUsuario) {
        logger.info("Solicitud de pólizas para usuario: {}", idUsuario);
        try {
            List<Seguro> seguros = segurosService.obtenerSegurosUsuario(idUsuario);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            logger.error("Error al obtener pólizas: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener pólizas");
        }
    }

    // Obtener solo seguros activos
    @GetMapping("/polizas/{idUsuario}/activos")
    public ResponseEntity<?> obtenerPolizasActivas(@PathVariable int idUsuario) {
        try {
            List<Seguro> seguros = segurosService.obtenerSegurosActivos(idUsuario);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener pólizas activas");
        }
    }

    // Obtener seguro por ID
    @GetMapping("/poliza/{idSeguro}")
    public ResponseEntity<?> obtenerPolizaPorId(@PathVariable int idSeguro) {
        try {
            Seguro seguro = segurosService.obtenerSeguroPorId(idSeguro);
            if (seguro == null) {
                return ResponseEntity.status(404).body("Póliza no encontrada");
            }
            return ResponseEntity.ok(seguro);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener póliza");
        }
    }

    // Crear nueva póliza
    @PostMapping("/polizas")
    public ResponseEntity<?> crearPoliza(@RequestBody Seguro seguro) {
        logger.info("Creando nueva póliza para usuario: {}", seguro.getUsuario().getIdUsuario());
        try {
            Seguro nuevoSeguro = segurosService.crearSeguro(seguro);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Póliza creada exitosamente");
            respuesta.put("data", nuevoSeguro);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al crear póliza: {}", e.getMessage());
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al crear póliza");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Actualizar póliza
    @PutMapping("/polizas/{idSeguro}")
    public ResponseEntity<?> actualizarPoliza(
            @PathVariable int idSeguro,
            @RequestBody Seguro seguro) {
        logger.info("Actualizando póliza ID: {}", idSeguro);
        try {
            seguro.setIdSeguro(idSeguro);
            Seguro actualizado = segurosService.actualizarSeguro(seguro);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Póliza actualizada exitosamente");
            respuesta.put("data", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al actualizar póliza: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al actualizar póliza");
        }
    }

    // Cancelar póliza
    @DeleteMapping("/polizas/{idSeguro}")
    public ResponseEntity<?> cancelarPoliza(@PathVariable int idSeguro) {
        logger.info("Cancelando póliza ID: {}", idSeguro);
        try {
            segurosService.eliminarSeguro(idSeguro);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Póliza cancelada exitosamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al cancelar póliza: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al cancelar póliza");
        }
    }

    // Obtener seguros próximos a vencer
    @GetMapping("/polizas/{idUsuario}/proximos-vencer")
    public ResponseEntity<?> obtenerProximosVencer(
            @PathVariable int idUsuario,
            @RequestParam(defaultValue = "30") int dias) {
        try {
            List<Seguro> seguros = segurosService.obtenerSegurosProximosVencer(idUsuario, dias);
            return ResponseEntity.ok(seguros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener seguros próximos a vencer");
        }
    }

    // Obtener beneficiarios de un seguro
    @GetMapping("/beneficiarios/{idSeguro}")
    public ResponseEntity<?> obtenerBeneficiarios(@PathVariable int idSeguro) {
        logger.info("Solicitud de beneficiarios para seguro: {}", idSeguro);
        try {
            List<BeneficiarioSeguro> beneficiarios = segurosService.obtenerBeneficiariosPorSeguro(idSeguro);
            return ResponseEntity.ok(beneficiarios);
        } catch (Exception e) {
            logger.error("Error al obtener beneficiarios: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener beneficiarios");
        }
    }

    // Agregar beneficiario
    @PostMapping("/beneficiarios")
    public ResponseEntity<?> agregarBeneficiario(@RequestBody BeneficiarioSeguro beneficiario) {
        logger.info("Agregando beneficiario para seguro: {}", beneficiario.getSeguro().getIdSeguro());
        try {
            BeneficiarioSeguro nuevo = segurosService.agregarBeneficiario(beneficiario);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Beneficiario agregado exitosamente");
            respuesta.put("data", nuevo);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al agregar beneficiario: {}", e.getMessage());
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al agregar beneficiario");
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    // Actualizar beneficiario
    @PutMapping("/beneficiarios/{idBeneficiario}")
    public ResponseEntity<?> actualizarBeneficiario(
            @PathVariable int idBeneficiario,
            @RequestBody BeneficiarioSeguro beneficiario) {
        try {
            beneficiario.setIdBeneficiario(idBeneficiario);
            BeneficiarioSeguro actualizado = segurosService.actualizarBeneficiario(beneficiario);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Beneficiario actualizado exitosamente");
            respuesta.put("data", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar beneficiario");
        }
    }

    // Eliminar beneficiario
    @DeleteMapping("/beneficiarios/{idBeneficiario}")
    public ResponseEntity<?> eliminarBeneficiario(@PathVariable int idBeneficiario) {
        try {
            segurosService.eliminarBeneficiario(idBeneficiario);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Beneficiario eliminado exitosamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar beneficiario");
        }
    }

    // Obtener pagos de un seguro
    @GetMapping("/pagos/{idSeguro}")
    public ResponseEntity<?> obtenerPagos(@PathVariable int idSeguro) {
        try {
            List<PagoSeguro> pagos = segurosService.obtenerPagosPorSeguro(idSeguro);
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener pagos");
        }
    }

    // Obtener pagos pendientes del usuario
    @GetMapping("/pagos/{idUsuario}/pendientes")
    public ResponseEntity<?> obtenerPagosPendientes(@PathVariable int idUsuario) {
        logger.info("Solicitud de pagos pendientes para usuario: {}", idUsuario);
        try {
            List<PagoSeguro> pagos = segurosService.obtenerPagosPendientes(idUsuario);
            Double total = segurosService.calcularTotalPendiente(idUsuario);

            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("pagos", pagos);
            respuesta.put("totalPendiente", total);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al obtener pagos pendientes: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener pagos pendientes");
        }
    }

    // Registrar pago
    @PostMapping("/pagos")
    public ResponseEntity<?> registrarPago(@RequestBody PagoSeguro pago) {
        logger.info("Registrando pago para seguro: {}", pago.getSeguro().getIdSeguro());
        try {
            PagoSeguro nuevo = segurosService.registrarPago(pago);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Pago registrado exitosamente");
            respuesta.put("data", nuevo);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al registrar pago: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al registrar pago");
        }
    }

    // Obtener trámites del usuario
    @GetMapping("/tramites/{idUsuario}")
    public ResponseEntity<?> obtenerTramites(@PathVariable int idUsuario) {
        logger.info("Solicitud de trámites para usuario: {}", idUsuario);
        try {
            List<TramiteSeguro> tramites = segurosService.obtenerTramitesUsuario(idUsuario);
            return ResponseEntity.ok(tramites);
        } catch (Exception e) {
            logger.error("Error al obtener trámites: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener trámites");
        }
    }

    // Obtener trámites pendientes
    @GetMapping("/tramites/{idUsuario}/pendientes")
    public ResponseEntity<?> obtenerTramitesPendientes(@PathVariable int idUsuario) {
        try {
            List<TramiteSeguro> tramites = segurosService.obtenerTramitesPendientes(idUsuario);
            return ResponseEntity.ok(tramites);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener trámites pendientes");
        }
    }

    // Crear trámite
    @PostMapping("/tramites")
    public ResponseEntity<?> crearTramite(@RequestBody TramiteSeguro tramite) {
        logger.info("Creando trámite para usuario: {}", tramite.getUsuario().getIdUsuario());
        try {
            TramiteSeguro nuevo = segurosService.crearTramite(tramite);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Trámite creado exitosamente");
            respuesta.put("data", nuevo);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al crear trámite: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al crear trámite");
        }
    }

    // Actualizar trámite
    @PutMapping("/tramites/{idTramite}")
    public ResponseEntity<?> actualizarTramite(
            @PathVariable int idTramite,
            @RequestBody TramiteSeguro tramite) {
        logger.info("Actualizando trámite ID: {}", idTramite);
        try {
            tramite.setIdTramite(idTramite);
            TramiteSeguro actualizado = segurosService.actualizarTramite(tramite);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Trámite actualizado exitosamente");
            respuesta.put("data", actualizado);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al actualizar trámite: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al actualizar trámite");
        }
    }

    // Eliminar trámite
    @DeleteMapping("/tramites/{idTramite}")
    public ResponseEntity<?> eliminarTramite(@PathVariable int idTramite) {
        logger.info("Eliminando trámite ID: {}", idTramite);
        try {
            segurosService.eliminarTramite(idTramite);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Trámite eliminado exitosamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al eliminar trámite: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al eliminar trámite");
        }
    }

    @GetMapping("/estadisticas/{idUsuario}")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable int idUsuario) {
        logger.info("Solicitud de estadísticas de seguros para usuario: {}", idUsuario);
        try {
            HashMap<String, Object> stats = segurosService.obtenerEstadisticas(idUsuario);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al obtener estadísticas");
        }
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        HashMap<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("servicio", "Gestión de Seguros");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }
}