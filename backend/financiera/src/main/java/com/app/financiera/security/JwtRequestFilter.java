package com.app.financiera.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        
        String dni = null;
        String jwtToken = null;

        // JWT Token está en el formato "Bearer token"
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                dni = jwtUtil.extractDni(jwtToken);
                Integer idUsuario = jwtUtil.extractIdUsuario(jwtToken);
                Integer idRol = jwtUtil.extractIdRol(jwtToken);
                
                // Agregar información al request para uso posterior
                request.setAttribute("dni", dni);
                request.setAttribute("idUsuario", idUsuario);
                request.setAttribute("idRol", idRol);
                
                // Autenticar al usuario en Spring Security
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Crear autoridad basada en el rol
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + idRol);
                    
                    // Crear token de autenticación
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(dni, null, Collections.singletonList(authority));
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    logger.debug("JWT Token válido para usuario: {} (ID: {}, Rol: {})", dni, idUsuario, idRol);
                }
            } catch (Exception e) {
                logger.warn("No se pudo extraer información del JWT Token: {}", e.getMessage());
            }
        } else {
            logger.debug("JWT Token no encontrado o no comienza con Bearer");
        }

        chain.doFilter(request, response);
    }
}
