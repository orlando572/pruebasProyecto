package com.app.financiera.dto;

import com.app.financiera.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Usuario usuario;
    private String mensaje;
}
