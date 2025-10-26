package com.app.financiera.controller;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.financiera.entity.AportePension;
import com.app.financiera.entity.SaldoPension;
import com.app.financiera.entity.TipoFondo;
import com.app.financiera.service.AportePensionService;
import com.app.financiera.service.SaldoPensionService;
import com.app.financiera.repository.TipoFondoRepository;
import com.app.financiera.util.AppSettings;

@RestController
@RequestMapping("/api/financiero")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class FinancieroController {

    private static final Logger logger = LoggerFactory.getLogger(FinancieroController.class);

    @Autowired
    private AportePensionService aportePensionService;

    @Autowired
    private SaldoPensionService saldoPensionService;

    @Autowired
    private TipoFondoRepository tipoFondoRepository;

    // Resumen financiero
    @GetMapping("/resumen/{idUsuario}")
    public ResponseEntity<?> obtenerResumenFinanciero(@PathVariable int idUsuario) {
        logger.info("Solicitud de resumen financiero para usuario ID: {}", idUsuario);
        try {
            HashMap<String, Object> resumen = new HashMap<>();

            Double saldoTotal = saldoPensionService.obtenerSaldoTotalUsuario(idUsuario);
            Double saldoDisponible = saldoPensionService.obtenerSaldosDisponibles(idUsuario);
            Double aportesUltimoYear = aportePensionService.obtenerTotalAportesUsuarioYear(
                    idUsuario, Calendar.getInstance().get(Calendar.YEAR));
            Double proyeccionMensual = (saldoTotal != null && saldoTotal > 0) ? saldoTotal / 240 : 0;

            List<AportePension> aportesONP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Pensiones");
            Double montoONP = aportesONP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            List<AportePension> aportesAFP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Financiera");
            Double montoAFP = aportesAFP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            resumen.put("saldoTotal", saldoTotal != null ? saldoTotal : 0);
            resumen.put("saldoDisponible", saldoDisponible != null ? saldoDisponible : 0);
            resumen.put("aportesUltimoYear", aportesUltimoYear != null ? aportesUltimoYear : 0);
            resumen.put("proyeccionPensionMensual", proyeccionMensual);
            resumen.put("aportesONP12m", montoONP);
            resumen.put("aportesAFP12m", montoAFP);
            resumen.put("mensaje", "Resumen obtenido exitosamente");

            logger.info("Resumen financiero generado correctamente para usuario ID: {}", idUsuario);
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            logger.error("Error al obtener resumen financiero para usuario ID {}: {}", idUsuario, e.getMessage(), e);
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al obtener resumen financiero");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Aportes
    @GetMapping("/aportes/{idUsuario}")
    public ResponseEntity<?> obtenerAportesUsuario(@PathVariable int idUsuario) {
        logger.info("Solicitud de aportes para usuario ID: {}", idUsuario);
        try {
            List<AportePension> aportes = aportePensionService.obtenerAportesUsuario(idUsuario);
            logger.info("Se obtuvieron {} aportes para usuario ID {}", aportes.size(), idUsuario);
            return ResponseEntity.ok(aportes);
        } catch (Exception e) {
            logger.error("Error al obtener aportes para usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener aportes");
        }
    }

    @GetMapping("/aportes/{idUsuario}/{year}")
    public ResponseEntity<?> obtenerAportesYear(@PathVariable int idUsuario, @PathVariable int year) {
        try {
            List<AportePension> aportes = aportePensionService.obtenerAportesUsuarioYear(idUsuario, year);
            return ResponseEntity.ok(aportes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener aportes del año");
        }
    }

    @GetMapping("/aportes/{idUsuario}/sistema/{sistema}")
    public ResponseEntity<?> obtenerAportesPorSistema(@PathVariable int idUsuario, @PathVariable String sistema) {
        try {
            List<AportePension> aportes = aportePensionService.obtenerAportesPorSistema(idUsuario, sistema);
            return ResponseEntity.ok(aportes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener aportes por sistema");
        }
    }

    @PostMapping("/aportes")
    public ResponseEntity<?> crearAporte(@RequestBody AportePension aporte) {
        try {
            AportePension nuevoAporte = aportePensionService.guardarAporte(aporte);
            HashMap<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Aporte registrado exitosamente");
            respuesta.put("data", nuevoAporte);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
            HashMap<String, Object> error = new HashMap<>();
            error.put("mensaje", "Error al registrar aporte");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Saldos
    @GetMapping("/saldos/{idUsuario}")
    public ResponseEntity<?> obtenerSaldosUsuario(@PathVariable int idUsuario) {
        logger.info("Solicitud de saldos para usuario ID: {}", idUsuario);
        try {
            List<SaldoPension> saldos = saldoPensionService.obtenerSaldosUsuario(idUsuario);
            logger.info("Se obtuvieron {} saldos para usuario ID {}", saldos.size(), idUsuario);
            return ResponseEntity.ok(saldos);
        } catch (Exception e) {
            logger.error("Error al obtener saldos para usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener saldos");
        }
    }

    @GetMapping("/saldos/{idUsuario}/total")
    public ResponseEntity<?> obtenerSaldoTotal(@PathVariable int idUsuario) {
        try {
            HashMap<String, Object> resultado = new HashMap<>();
            resultado.put("saldoTotal", saldoPensionService.obtenerSaldoTotalUsuario(idUsuario));
            resultado.put("saldoDisponible", saldoPensionService.obtenerSaldosDisponibles(idUsuario));
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener saldo total");
        }
    }

    // Estadistica
    @GetMapping("/estadisticas/{idUsuario}")
    public ResponseEntity<?> obtenerEstadisticas(@PathVariable int idUsuario) {
        logger.info("Solicitud de estadísticas para usuario ID: {}", idUsuario);
        try {
            HashMap<String, Object> stats = new HashMap<>();
            int yearActual = Calendar.getInstance().get(Calendar.YEAR);
            List<HashMap<String, Object>> aportesPorYear = new ArrayList<>();

            for (int i = 2; i >= 0; i--) {
                int year = yearActual - i;
                Double total = aportePensionService.obtenerTotalAportesUsuarioYear(idUsuario, year);
                HashMap<String, Object> yearData = new HashMap<>();
                yearData.put("year", year);
                yearData.put("total", total != null ? total : 0);
                aportesPorYear.add(yearData);
            }

            stats.put("aportesPorYear", aportesPorYear);
            stats.put("saldoTotal", saldoPensionService.obtenerSaldoTotalUsuario(idUsuario));
            stats.put("rentabilidadPromedio", 5.2);

            logger.info("Estadísticas generadas correctamente para usuario ID {}", idUsuario);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas para usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener estadísticas");
        }
    }

    // Tipos de fondo
    @GetMapping("/fondos")
    public ResponseEntity<?> obtenerTodosFondos() {
        try {
            List<TipoFondo> fondos = tipoFondoRepository.findActivos();
            return ResponseEntity.ok(fondos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener fondos");
        }
    }

    @GetMapping("/fondos/{idUsuario}")
    public ResponseEntity<?> obtenerFondosDisponibles(@PathVariable int idUsuario) {
        try {
            List<TipoFondo> fondos = tipoFondoRepository.findActivos();
            return ResponseEntity.ok(fondos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al obtener fondos disponibles");
        }
    }

    // Analisis comparativo
    @GetMapping("/comparativo/{idUsuario}")
    public ResponseEntity<?> obtenerComparativo(@PathVariable int idUsuario) {
        logger.info("Solicitud de comparativo financiero para usuario ID: {}", idUsuario);
        try {
            HashMap<String, Object> comparativo = new HashMap<>();

            List<AportePension> aportesONP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Pensiones");
            Double montoONP = aportesONP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            List<AportePension> aportesAFP = aportePensionService.obtenerAportesPorSistema(idUsuario, "Financiera");
            Double montoAFP = aportesAFP.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0).sum();

            comparativo.put("onp", montoONP);
            comparativo.put("afp", montoAFP);
            comparativo.put("total", montoONP + montoAFP);
            comparativo.put("porcentajeONP", montoONP + montoAFP > 0 ? (montoONP / (montoONP + montoAFP)) * 100 : 0);
            comparativo.put("porcentajeAFP", montoONP + montoAFP > 0 ? (montoAFP / (montoONP + montoAFP)) * 100 : 0);

            logger.info("Comparativo generado correctamente para usuario ID {}", idUsuario);
            return ResponseEntity.ok(comparativo);
        } catch (Exception e) {
            logger.error("Error al obtener comparativo financiero para usuario ID {}: {}", idUsuario, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al obtener comparativo");
        }
    }
}