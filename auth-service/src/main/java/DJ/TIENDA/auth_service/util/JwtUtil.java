package DJ.TIENDA.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Spring lo gestiona como un componente reutilizable
public class JwtUtil {

    @Value("${jwt.secret}")       // Lee la clave secreta del application.properties
    private String secret;

    @Value("${jwt.expiration}")   // Lee el tiempo de expiración del application.properties
    private Long expiration;

    // Convierte el String secret en una clave criptográfica válida para HS256
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un JWT con el email y rol del usuario como datos dentro del token
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)                                        // Quién es el usuario
                .claim("rol", rol)                                     // Su rol dentro del token
                .issuedAt(new Date())                                  // Cuándo se generó
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Cuándo expira
                .signWith(getKey())                                    // Firma con nuestra clave secreta
                .compact();
    }

    // Valida que el token sea correcto y no haya expirado
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey()) // Verifica la firma
                    .build()
                    .parseSignedClaims(token);
            return true; // Si no lanza excepción, el token es válido
        } catch (Exception e) {
            return false; // Token inválido o expirado
        }
    }

    // Extrae el email (subject) guardado dentro del token
    public String obtenerEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Extrae el rol guardado dentro del token
    public String obtenerRol(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("rol", String.class);
    }
}