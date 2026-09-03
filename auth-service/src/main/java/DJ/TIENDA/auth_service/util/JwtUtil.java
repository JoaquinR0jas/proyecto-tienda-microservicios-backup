package DJ.TIENDA.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")       // clave secreta del application.properties
    private String secret;

    @Value("${jwt.expiration}")   // expiracion del application.properties
    private Long expiration;

    // Convierte el String secret en una clave criptografica valida para HS256
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un JWT con el email y rol del usuario dentro del token
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)                                        // quien es el usuario
                .claim("rol", rol)                                     // su rol dentro del token
                .issuedAt(new Date())                                  // cuando se genero
                .expiration(new Date(System.currentTimeMillis() + expiration)) // cuando expira
                .signWith(getKey())                                    // firma con nuestra clave secreta
                .compact();
    }

    // Valida que el token sea correcto y no haya expirado
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
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