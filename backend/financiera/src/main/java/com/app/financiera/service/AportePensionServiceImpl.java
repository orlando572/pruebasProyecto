package com.app.financiera.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.financiera.entity.AportePension;
import com.app.financiera.entity.SaldoPension;
import com.app.financiera.entity.Usuario;
import com.app.financiera.entity.Institucion;
import com.app.financiera.repository.AportePensionRepository;
import com.app.financiera.repository.SaldoPensionRepository;
import com.app.financiera.repository.UsuarioRepository;
import com.app.financiera.repository.InstitucionRepository;


@Service
public class AportePensionServiceImpl implements AportePensionService {

    private static final Logger logger = LoggerFactory.getLogger(AportePensionServiceImpl.class);

    @Autowired
    private AportePensionRepository aportePensionRepository;

    @Autowired
    private SaldoPensionRepository saldoPensionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private InstitucionRepository institucionRepository;

    @Override
    public List<AportePension> obtenerAportesUsuario(int idUsuario) {
        return aportePensionRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public List<AportePension> obtenerAportesUsuarioYear(int idUsuario, int year) {
        return aportePensionRepository.findByUsuarioAndYear(idUsuario, year);
    }

    @Override
    public List<AportePension> obtenerAportesUltimoYear(int idUsuario) {
        return aportePensionRepository.findAportesUltimoYear(idUsuario);
    }

    @Override
    public List<AportePension> obtenerAportesPorSistema(int idUsuario, String sistema) {
        return aportePensionRepository.findByUsuarioAndSistema(idUsuario, sistema);
    }

    @Override
    public Double obtenerTotalAportesUsuario(int idUsuario) {
        return aportePensionRepository.sumAportesUsuario(idUsuario);
    }

    @Override
    public Double obtenerTotalAportesUsuarioYear(int idUsuario, int year) {
        return aportePensionRepository.sumAportesUsuarioYear(idUsuario, year);
    }

    @Override
    public AportePension guardarAporte(AportePension aporte) {
        logger.info("Guardando aporte para usuario: {}", aporte.getUsuario().getIdUsuario());

        // SINCRONIZAR INSTITUCIÓN CON EL PERFIL ACTUAL DEL USUARIO
        sincronizarInstitucionConUsuario(aporte);

        AportePension aporteGuardado = aportePensionRepository.save(aporte);

        // Actualizar saldo automáticamente
        actualizarSaldoUsuario(aporte.getUsuario().getIdUsuario());

        return aporteGuardado;
    }

    @Override
    public AportePension actualizarAporte(AportePension aporte) {
        logger.info("Actualizando aporte: {}", aporte.getIdAporte());

        // SINCRONIZAR INSTITUCIÓN CON EL PERFIL ACTUAL DEL USUARIO
        sincronizarInstitucionConUsuario(aporte);

        AportePension aporteActualizado = aportePensionRepository.save(aporte);

        // Actualizar saldo automáticamente
        actualizarSaldoUsuario(aporte.getUsuario().getIdUsuario());

        return aporteActualizado;
    }

    @Override
    public void eliminarAporte(int idAporte) {
        logger.info("Eliminando aporte: {}", idAporte);

        // Obtener el aporte antes de eliminarlo para saber el usuario
        Optional<AportePension> aporteOpt = aportePensionRepository.findById(idAporte);

        if (aporteOpt.isPresent()) {
            int idUsuario = aporteOpt.get().getUsuario().getIdUsuario();
            aportePensionRepository.deleteById(idAporte);

            // Actualizar saldo automáticamente
            actualizarSaldoUsuario(idUsuario);
        } else {
            aportePensionRepository.deleteById(idAporte);
        }
    }

    @Override
    public List<AportePension> obtenerTodos() {
        return aportePensionRepository.findAll();
    }

    private void sincronizarInstitucionConUsuario(AportePension aporte) {
        try {
            int idUsuario = aporte.getUsuario().getIdUsuario();
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

            if (!usuarioOpt.isPresent()) {
                logger.warn("Usuario {} no encontrado, no se sincroniza institución", idUsuario);
                return;
            }

            Usuario usuario = usuarioOpt.get();
            logger.info("Sincronizando institución para usuario {}: tipoRegimen={}, AFP={}",
                    idUsuario, usuario.getTipoRegimen(),
                    usuario.getAfp() != null ? usuario.getAfp().getNombre() : "null");

            // Determinar la institución según el perfil del usuario
            Institucion institucionCorrecta = null;

            // CASO 1: Usuario está en ONP
            if ("ONP".equalsIgnoreCase(usuario.getTipoRegimen())) {
                // Buscar institución ONP (tipo="Pensiones")
                List<Institucion> instituciones = institucionRepository.findAll();

                // Buscar por tipo="Pensiones" (solo hay una: ONP)
                institucionCorrecta = instituciones.stream()
                        .filter(i -> "Pensiones".equalsIgnoreCase(i.getTipo()))
                        .findFirst()
                        .orElse(null);

                logger.info("Usuario en ONP, institución asignada: {}",
                        institucionCorrecta != null ? institucionCorrecta.getNombre() : "null");
            }
            // CASO 2: Usuario está en AFP
            else if ("AFP".equalsIgnoreCase(usuario.getTipoRegimen()) && usuario.getAfp() != null) {
                // Buscar institución que corresponda a la AFP del usuario
                List<Institucion> instituciones = institucionRepository.findAll();
                String nombreAFP = usuario.getAfp().getNombre(); // Ej: "Integra", "Prima", "Habitat", "Profuturo"

                // Buscar por tipo="Financiera" y nombre que contenga el nombre de la AFP
                institucionCorrecta = instituciones.stream()
                        .filter(i -> "Financiera".equalsIgnoreCase(i.getTipo()) &&
                                i.getNombre() != null &&
                                i.getNombre().toUpperCase().contains(nombreAFP.toUpperCase()))
                        .findFirst()
                        .orElse(null);

                // Si no encuentra por nombre, buscar cualquier institución de tipo Financiera
                if (institucionCorrecta == null) {
                    logger.warn("No se encontró AFP con nombre '{}', buscando cualquier AFP", nombreAFP);
                    institucionCorrecta = instituciones.stream()
                            .filter(i -> "Financiera".equalsIgnoreCase(i.getTipo()))
                            .findFirst()
                            .orElse(null);
                }

                logger.info("Usuario en AFP '{}', institución asignada: {}",
                        nombreAFP, institucionCorrecta != null ? institucionCorrecta.getNombre() : "null");
            }

            // Asignar la institución correcta al aporte
            if (institucionCorrecta != null) {
                aporte.setInstitucion(institucionCorrecta);
                logger.info("Institución sincronizada: {} (tipo: {})",
                        institucionCorrecta.getNombre(), institucionCorrecta.getTipo());
            } else {
                logger.warn("No se encontró institución adecuada para el usuario {}", idUsuario);
            }

        } catch (Exception e) {
            logger.error("Error al sincronizar institución del aporte: {}", e.getMessage(), e);
        }
    }

    private void actualizarSaldoUsuario(int idUsuario) {
        try {
            logger.info("Actualizando saldo para usuario: {}", idUsuario);

            // Obtener todos los aportes del usuario
            List<AportePension> aportes = aportePensionRepository.findByUsuarioId(idUsuario);

            if (aportes.isEmpty()) {
                logger.info("Usuario {} no tiene aportes, no se crea saldo", idUsuario);
                return;
            }

            // Calcular saldo total sumando todos los aportes
            double saldoTotal = aportes.stream()
                    .mapToDouble(a -> a.getMontoAporte() != null ? a.getMontoAporte() : 0.0)
                    .sum();

            // Calcular saldo disponible (90% del total como ejemplo)
            double saldoDisponible = saldoTotal * 0.9;

            // Calcular rentabilidad (5% anual acumulado como ejemplo)
            double rentabilidadAcumulada = saldoTotal * 0.05;

            // Buscar si ya existe un saldo para este usuario
            List<SaldoPension> saldosExistentes = saldoPensionRepository.findByUsuarioId(idUsuario);

            SaldoPension saldo;
            if (!saldosExistentes.isEmpty()) {
                // Actualizar el primer saldo encontrado
                saldo = saldosExistentes.get(0);
                logger.info("Actualizando saldo existente ID: {}", saldo.getIdSaldo());
            } else {
                // Crear nuevo saldo
                saldo = new SaldoPension();
                saldo.setUsuario(aportes.get(0).getUsuario());
                saldo.setEstado("Activo");
                logger.info("Creando nuevo saldo para usuario: {}", idUsuario);
            }

            // Actualizar valores
            saldo.setSaldoTotal(saldoTotal);
            saldo.setSaldoDisponible(saldoDisponible);
            saldo.setSaldoCIC(saldoTotal * 0.6); // 60% en CIC
            saldo.setSaldoCV(saldoTotal * 0.4);  // 40% en CV
            saldo.setRentabilidadAcumulada(rentabilidadAcumulada);
            saldo.setFechaCorte(LocalDate.now());
            saldo.setFechaActualizacion(LocalDate.now());

            // Si tiene tipo de fondo, asignarlo
            if (aportes.get(0).getTipoFondo() != null) {
                saldo.setTipoFondo(aportes.get(0).getTipoFondo());
            }

            // Guardar saldo
            saldoPensionRepository.save(saldo);
            logger.info("Saldo actualizado exitosamente. Total: S/ {}", saldoTotal);

        } catch (Exception e) {
            logger.error("Error al actualizar saldo del usuario {}: {}", idUsuario, e.getMessage(), e);
        }
    }
}