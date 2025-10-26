package com.app.financiera.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.financiera.entity.SaldoPension;
import com.app.financiera.repository.SaldoPensionRepository;

@Service
public class SaldoPensionServiceImpl implements SaldoPensionService {

    @Autowired
    private SaldoPensionRepository saldoPensionRepository;

    @Override
    public List<SaldoPension> obtenerSaldosUsuario(int idUsuario) {
        return saldoPensionRepository.findByUsuarioId(idUsuario);
    }

    @Override
    public SaldoPension obtenerSaldoPorAfp(int idUsuario, int idAfp) {
        Optional<SaldoPension> saldo = saldoPensionRepository.findByUsuarioAndAfp(idUsuario, idAfp);
        return saldo.orElse(null);
    }

    @Override
    public SaldoPension obtenerSaldoONP(int idUsuario) {
        Optional<SaldoPension> saldo = saldoPensionRepository.findONPSaldo(idUsuario);
        return saldo.orElse(null);
    }

    @Override
    public Double obtenerSaldoTotalUsuario(int idUsuario) {
        return saldoPensionRepository.sumSaldosUsuario(idUsuario);
    }

    @Override
    public Double obtenerSaldosDisponibles(int idUsuario) {
        return saldoPensionRepository.sumSaldosDisponibles(idUsuario);
    }

    @Override
    public SaldoPension guardarSaldo(SaldoPension saldo) {
        return saldoPensionRepository.save(saldo);
    }

    @Override
    public SaldoPension actualizarSaldo(SaldoPension saldo) {
        return saldoPensionRepository.save(saldo);
    }

    @Override
    public void eliminarSaldo(int idSaldo) {
        saldoPensionRepository.deleteById(idSaldo);
    }

    @Override
    public List<SaldoPension> obtenerTodos() {
        return saldoPensionRepository.findAll();
    }
}