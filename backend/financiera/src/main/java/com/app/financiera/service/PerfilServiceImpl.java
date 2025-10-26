package com.app.financiera.service;

import com.app.financiera.dto.PerfilUsuarioDTO;
import com.app.financiera.entity.Usuario;
import com.app.financiera.entity.Afp;
import com.app.financiera.repository.UsuarioRepository;
import com.app.financiera.repository.AfpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PerfilServiceImpl implements PerfilService {

    private static final Logger logger = LoggerFactory.getLogger(PerfilServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AfpRepository afpRepository;

    @Override
    public PerfilUsuarioDTO obtenerPerfil(int idUsuario) {
        logger.info("Obteniendo perfil del usuario ID: {}", idUsuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (!usuarioOpt.isPresent()) {
            logger.error("Usuario no encontrado con ID: {}", idUsuario);
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        return convertirADTO(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizarPerfil(int idUsuario, PerfilUsuarioDTO perfilDTO) {
        logger.info("Actualizando perfil del usuario ID: {}", idUsuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (!usuarioOpt.isPresent()) {
            logger.error("Usuario no encontrado con ID: {}", idUsuario);
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // Validar DNI único si cambió
        if (!usuario.getDni().equals(perfilDTO.getDni())) {
            if (!validarDniUnico(perfilDTO.getDni(), idUsuario)) {
                throw new RuntimeException("El DNI ya está registrado por otro usuario");
            }
        }

        // Validar correo único si cambió
        if (!usuario.getCorreo().equals(perfilDTO.getCorreo())) {
            if (!validarCorreoUnico(perfilDTO.getCorreo(), idUsuario)) {
                throw new RuntimeException("El correo ya está registrado por otro usuario");
            }
        }

        // Actualizar información personal
        usuario.setNombre(perfilDTO.getNombre());
        usuario.setApellido(perfilDTO.getApellido());
        usuario.setDni(perfilDTO.getDni());
        usuario.setFechaNacimiento(perfilDTO.getFechaNacimiento());
        usuario.setGenero(perfilDTO.getGenero());
        usuario.setEstadoCivil(perfilDTO.getEstadoCivil());

        // Actualizar información de contacto
        usuario.setCorreo(perfilDTO.getCorreo());
        usuario.setTelefono(perfilDTO.getTelefono());
        usuario.setDireccion(perfilDTO.getDireccion());
        usuario.setDistrito(perfilDTO.getDistrito());
        usuario.setProvincia(perfilDTO.getProvincia());
        usuario.setDepartamento(perfilDTO.getDepartamento());

        // Actualizar información laboral
        usuario.setCentroTrabajo(perfilDTO.getCentroTrabajo());
        usuario.setSalarioActual(perfilDTO.getSalarioActual());
        usuario.setFechaIngresoTrabajo(perfilDTO.getFechaIngresoTrabajo());
        usuario.setTipoContrato(perfilDTO.getTipoContrato());

        // Actualizar información de pensiones
        usuario.setCuspp(perfilDTO.getCuspp());
        usuario.setTipoRegimen(perfilDTO.getTipoRegimen());
        usuario.setFechaAfiliacion(perfilDTO.getFechaAfiliacion());

        // Actualizar AFP si cambió
        if (perfilDTO.getIdAfp() != null && perfilDTO.getIdAfp() > 0) {
            Optional<Afp> afpOpt = afpRepository.findById(perfilDTO.getIdAfp());
            if (afpOpt.isPresent()) {
                usuario.setAfp(afpOpt.get());
            }
        } else {
            usuario.setAfp(null);
        }

        // Actualizar preferencias
        usuario.setNotificacionesEmail(perfilDTO.isNotificacionesEmail());
        usuario.setNotificacionesSms(perfilDTO.isNotificacionesSms());

        // Actualizar foto de perfil si se proporcionó
        if (perfilDTO.getFotoPerfil() != null && !perfilDTO.getFotoPerfil().isEmpty()) {
            usuario.setFotoPerfil(perfilDTO.getFotoPerfil());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        logger.info("Perfil actualizado exitosamente para usuario ID: {}", idUsuario);

        return actualizado;
    }

    @Override
    @Transactional
    public Usuario actualizarFotoPerfil(int idUsuario, String fotoPerfil) {
        logger.info("Actualizando foto de perfil del usuario ID: {}", idUsuario);

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (!usuarioOpt.isPresent()) {
            logger.error("Usuario no encontrado con ID: {}", idUsuario);
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setFotoPerfil(fotoPerfil);

        Usuario actualizado = usuarioRepository.save(usuario);
        logger.info("Foto de perfil actualizada exitosamente para usuario ID: {}", idUsuario);

        return actualizado;
    }

    @Override
    public boolean validarDniUnico(String dni, int idUsuarioActual) {
        Usuario usuarioExistente = usuarioRepository.findByDni(dni);
        return usuarioExistente == null || usuarioExistente.getIdUsuario() == idUsuarioActual;
    }

    @Override
    public boolean validarCorreoUnico(String correo, int idUsuarioActual) {
        Usuario usuarioExistente = usuarioRepository.findByCorreo(correo);
        return usuarioExistente == null || usuarioExistente.getIdUsuario() == idUsuarioActual;
    }

    private PerfilUsuarioDTO convertirADTO(Usuario usuario) {
        PerfilUsuarioDTO dto = new PerfilUsuarioDTO();

        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setDni(usuario.getDni());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setGenero(usuario.getGenero());
        dto.setEstadoCivil(usuario.getEstadoCivil());

        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setDistrito(usuario.getDistrito());
        dto.setProvincia(usuario.getProvincia());
        dto.setDepartamento(usuario.getDepartamento());

        dto.setCentroTrabajo(usuario.getCentroTrabajo());
        dto.setSalarioActual(usuario.getSalarioActual());
        dto.setFechaIngresoTrabajo(usuario.getFechaIngresoTrabajo());
        dto.setTipoContrato(usuario.getTipoContrato());

        dto.setCuspp(usuario.getCuspp());
        dto.setIdAfp(usuario.getAfp() != null ? usuario.getAfp().getIdAfp() : null);
        dto.setTipoRegimen(usuario.getTipoRegimen());
        dto.setFechaAfiliacion(usuario.getFechaAfiliacion());

        dto.setNotificacionesEmail(usuario.isNotificacionesEmail());
        dto.setNotificacionesSms(usuario.isNotificacionesSms());

        dto.setFotoPerfil(usuario.getFotoPerfil());

        // Información adicional de solo lectura
        dto.setNombreRol(usuario.getRol() != null ? usuario.getRol().getNombreRol() : null);
        dto.setNombreAfp(usuario.getAfp() != null ? usuario.getAfp().getNombre() : null);
        dto.setEstado(usuario.getEstado());

        return dto;
    }
}