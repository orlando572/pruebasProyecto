package com.app.financiera.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.financiera.entity.RolUsuario;
import com.app.financiera.repository.RolUsuarioRepository;

@Service
public class RolUsuarioServiceImpl implements RolUsuarioService {

    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;

    @Override
    public List<RolUsuario> listarRoles() {
        return rolUsuarioRepository.findAll();
    }
}