package com.app.financiera.controller;

import com.app.financiera.dto.PerfilUsuarioDTO;
import com.app.financiera.entity.Usuario;
import com.app.financiera.service.PerfilService;
import com.app.financiera.util.AppSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
@CrossOrigin(origins = AppSettings.URL_CROSS_ORIGIN)
public class PerfilController {

    private static final Logger logger = LoggerFactory.getLogger(PerfilController.class);

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> obtenerPerfil(@PathVariable int idUsuario) {
        logger.info("Solicitud de perfil para usuario ID: {}", idUsuario);
        try {
            PerfilUsuarioDTO perfil = perfilService.obtenerPerfil(idUsuario);
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            logger.error("Error al obtener perfil del usuario ID {}: {}", idUsuario, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al obtener el perfil");
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> actualizarPerfil(
            @PathVariable int idUsuario,
            @RequestBody PerfilUsuarioDTO perfilDTO) {

        logger.info("Actualizando perfil del usuario ID: {}", idUsuario);
        Map<String, Object> respuesta = new HashMap<>();

        try {
            Usuario usuarioActualizado = perfilService.actualizarPerfil(idUsuario, perfilDTO);

            respuesta.put("mensaje", "Perfil actualizado exitosamente");
            respuesta.put("data", usuarioActualizado);

            logger.info("Perfil actualizado exitosamente para usuario ID: {}", idUsuario);
            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            logger.error("Error de validación al actualizar perfil: {}", e.getMessage());
            respuesta.put("mensaje", e.getMessage());
            return ResponseEntity.status(400).body(respuesta);

        } catch (Exception e) {
            logger.error("Error al actualizar perfil del usuario ID {}: {}", idUsuario, e.getMessage());
            respuesta.put("mensaje", "Error al actualizar el perfil");
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @PatchMapping("/{idUsuario}/foto")
    public ResponseEntity<?> actualizarFotoPerfil(
            @PathVariable int idUsuario,
            @RequestBody Map<String, String> body) {

        logger.info("Actualizando foto de perfil del usuario ID: {}", idUsuario);
        Map<String, Object> respuesta = new HashMap<>();

        try {
            String fotoPerfil = body.get("fotoPerfil");

            if (fotoPerfil == null || fotoPerfil.isEmpty()) {
                respuesta.put("mensaje", "La foto de perfil no puede estar vacía");
                return ResponseEntity.status(400).body(respuesta);
            }

            Usuario usuarioActualizado = perfilService.actualizarFotoPerfil(idUsuario, fotoPerfil);

            respuesta.put("mensaje", "Foto de perfil actualizada exitosamente");
            respuesta.put("fotoPerfil", usuarioActualizado.getFotoPerfil());

            logger.info("Foto de perfil actualizada exitosamente para usuario ID: {}", idUsuario);
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error al actualizar foto de perfil del usuario ID {}: {}", idUsuario, e.getMessage());
            respuesta.put("mensaje", "Error al actualizar la foto de perfil");
            respuesta.put("error", e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }

    @GetMapping("/validar-dni")
    public ResponseEntity<?> validarDni(
            @RequestParam String dni,
            @RequestParam int idUsuarioActual) {

        try {
            boolean esUnico = perfilService.validarDniUnico(dni, idUsuarioActual);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("disponible", esUnico);
            respuesta.put("mensaje", esUnico ? "DNI disponible" : "DNI ya registrado");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al validar DNI: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al validar DNI");
        }
    }

    @GetMapping("/validar-correo")
    public ResponseEntity<?> validarCorreo(
            @RequestParam String correo,
            @RequestParam int idUsuarioActual) {

        try {
            boolean esUnico = perfilService.validarCorreoUnico(correo, idUsuarioActual);
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("disponible", esUnico);
            respuesta.put("mensaje", esUnico ? "Correo disponible" : "Correo ya registrado");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            logger.error("Error al validar correo: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error al validar correo");
        }
    }
}