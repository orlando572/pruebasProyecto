package com.app.financiera.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Clave secreta para firmar el JWT (en producción debe estar en variables de entorno)
    private static final String SECRET_KEY = "sumaq_seguros_secret_key_2025_financiera_app_jwt_token_security";
    
    // Tiempo de expiración: 24 horas
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generar token para un usuario
    public String generateToken(String dni, Integer idUsuario, Integer idRol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUsuario", idUsuario);
        claims.put("idRol", idRol);
        return createToken(claims, dni);
    }

    // Crear el token JWT
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer el DNI del token
    public String extractDni(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer el ID del usuario del token
    public Integer extractIdUsuario(String token) {
        return extractClaim(token, claims -> claims.get("idUsuario", Integer.class));
    }

    // Extraer el ID del rol del token
    public Integer extractIdRol(String token) {
        return extractClaim(token, claims -> claims.get("idRol", Integer.class));
    }

    // Extraer fecha de expiración
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extraer un claim específico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extraer todos los claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar si el token ha expirado
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validar el token
    public Boolean validateToken(String token, String dni) {
        final String extractedDni = extractDni(token);
        return (extractedDni.equals(dni) && !isTokenExpired(token));
    }

    // Validar solo si el token es válido (sin comparar usuario)
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
