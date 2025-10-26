package com.app.financiera.service;

import java.util.List;
import com.app.financiera.entity.AportePension;

public interface AportePensionService {

    List<AportePension> obtenerAportesUsuario(int idUsuario);

    List<AportePension> obtenerAportesUsuarioYear(int idUsuario, int year);

    List<AportePension> obtenerAportesUltimoYear(int idUsuario);

    List<AportePension> obtenerAportesPorSistema(int idUsuario, String sistema);

    Double obtenerTotalAportesUsuario(int idUsuario);

    Double obtenerTotalAportesUsuarioYear(int idUsuario, int year);

    AportePension guardarAporte(AportePension aporte);

    AportePension actualizarAporte(AportePension aporte);

    void eliminarAporte(int idAporte);

    List<AportePension> obtenerTodos();
}