package com.app.financiera.service;

import java.util.List;
import com.app.financiera.entity.SaldoPension;

public interface SaldoPensionService {

    List<SaldoPension> obtenerSaldosUsuario(int idUsuario);

    SaldoPension obtenerSaldoPorAfp(int idUsuario, int idAfp);

    SaldoPension obtenerSaldoONP(int idUsuario);

    Double obtenerSaldoTotalUsuario(int idUsuario);

    Double obtenerSaldosDisponibles(int idUsuario);

    SaldoPension guardarSaldo(SaldoPension saldo);

    SaldoPension actualizarSaldo(SaldoPension saldo);

    void eliminarSaldo(int idSaldo);

    List<SaldoPension> obtenerTodos();
}