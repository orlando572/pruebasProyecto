package com.app.financiera.service;

import java.util.List;
import com.app.financiera.entity.Afp;

public interface AfpService {

    List<Afp> listarAfps();

    Afp buscarPorId(int id);

    Afp buscarPorNombreExacto(String nombre);

    List<Afp> buscarPorNombre(String busqueda);

    Afp buscarPorCodigoSbs(String codigoSbs);

    List<Afp> buscarPorEstado(String estado);

    Afp guardarAfp(Afp afp);

    Afp actualizarAfp(Afp afp);

    void eliminarAfp(int id);

    java.util.HashMap<String, Object> obtenerEstadisticas();
}