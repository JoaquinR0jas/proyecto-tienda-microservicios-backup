package DJ.TIENDA.auth_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    @Value("${jwt.expiration}")   // cuanto dura el token (1 hora)
    private Long expiration;

    // Convierte el String secret en una clave criptografica valida para HS256
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Genera un JWT con el id, el email y el rol del usuario.
    // El id nos sirve para que el gateway sepa de quien es la sesion.
    public String generarToken(Long id, String email, String rol) {
        return Jwts.builder()
                .subject(email)                                        // quien es el usuario
                .claim("id", id)                                       // id del usuario en la base
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

    // Devuelve todos los datos que vienen dentro del token
    public Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Devuelve los datos del token aunque ya haya vencido, siempre que la firma
    // sea correcta. Sirve para el refresh: si el token vencio hace poco, el
    // usuario igual puede pedir uno nuevo sin escribir la clave otra vez.
    public Claims obtenerClaimsAunqueEsteVencido(String token) {
        try {
            return obtenerClaims(token);
        } catch (ExpiredJwtException e) {
            // La firma ya se reviso antes de llegar a esta excepcion,
            // asi que los datos del token siguen siendo confiables.
            return e.getClaims();
        }
    }

    // Extrae el email (subject) guardado dentro del token
    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Extrae el id del usuario guardado dentro del token
    public Long obtenerId(String token) {
        Number id = obtenerClaims(token).get("id", Number.class);
        return id == null ? null : id.longValue();
    }

    // Extrae el rol guardado dentro del token
    public String obtenerRol(String token) {
        return obtenerClaims(token).get("rol", String.class);
    }
}
