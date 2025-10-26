package com.app.financiera.service;

import com.app.financiera.dto.PerfilUsuarioDTO;
import com.app.financiera.entity.Usuario;

public interface PerfilService {

    PerfilUsuarioDTO obtenerPerfil(int idUsuario);

    Usuario actualizarPerfil(int idUsuario, PerfilUsuarioDTO perfilDTO);

    Usuario actualizarFotoPerfil(int idUsuario, String fotoPerfil);

    boolean validarDniUnico(String dni, int idUsuarioActual);

    boolean validarCorreoUnico(String correo, int idUsuarioActual);
}