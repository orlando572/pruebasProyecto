package com.app.financiera.service;

public interface ChatBotService {

    String procesarMensaje(String mensaje, Integer idUsuario);

    boolean solicitarContactoAsesor(Integer idUsuario, String motivo);

    String obtenerContextoUsuario(Integer idUsuario);
}