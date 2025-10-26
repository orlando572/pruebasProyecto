package com.app.financiera.service;

import java.util.List;
import com.app.financiera.entity.Usuario;

public interface UsuarioService {

    Usuario buscarPorDni(String dni);

    // CRUD básico
    Usuario registraUsuario(Usuario obj);
    Usuario actualizaUsuario(Usuario obj);
    void eliminaUsuario(int id);
    List<Usuario> listaTodos();
    List<Usuario> buscarUsuarioPorId(int id);

    // Búsquedas adicionales para admin
    List<Usuario> buscarPorNombreODni(String busqueda);
    List<Usuario> buscarPorEstado(String estado);
    List<Usuario> buscarPorRol(int idRol);

    // Estadísticas
    java.util.HashMap<String, Object> obtenerEstadisticas();
}