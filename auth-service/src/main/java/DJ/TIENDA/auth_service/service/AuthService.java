package DJ.TIENDA.auth_service.service;

import DJ.TIENDA.auth_service.client.UsuarioClient;
import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import DJ.TIENDA.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioClient usuarioClient; // Para consultar ms-usuarios via Feign

    @Autowired
    private JwtUtil jwtUtil; // Para generar y validar tokens

    // Lógica del login: verifica credenciales y genera el JWT
    public String login(String email, String password) {
        UsuarioDTO usuario = usuarioClient.buscarPorEmail(email);

        // ⚠️ Por ahora comparamos en texto plano, cuando agreguemos BCrypt esto cambia
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());
    }

    // Lógica de validación: verifica si un token JWT es válido
    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }

    // Extrae el email del token (útil para otros ms que necesiten saber quién es el usuario)
    public String obtenerEmail(String token) {
        return jwtUtil.obtenerEmail(token);
    }

    // Extrae el rol del token
    public String obtenerRol(String token) {
        return jwtUtil.obtenerRol(token);
    }
}