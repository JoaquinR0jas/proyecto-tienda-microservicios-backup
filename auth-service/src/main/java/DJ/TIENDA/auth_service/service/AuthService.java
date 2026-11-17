package DJ.TIENDA.auth_service.service;

import DJ.TIENDA.auth_service.client.UsuarioClient;
import DJ.TIENDA.auth_service.dto.LoginResponseDTO;
import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import DJ.TIENDA.auth_service.util.JwtUtil;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioClient usuarioClient; // Para consultar ms-usuarios via Feign

    @Autowired
    private JwtUtil jwtUtil; // Para generar y validar tokens

    // Lógica del login: verifica credenciales y genera el JWT junto con los datos de la sesion
    public LoginResponseDTO login(String email, String password) {
        UsuarioDTO usuario;
        try {
            usuario = usuarioClient.buscarPorEmail(email);
        } catch (FeignException.NotFound e) {
            usuario = null; // ms-usuarios responde 404 cuando el email no existe
        }

        // ⚠️ Por ahora comparamos en texto plano, cuando agreguemos BCrypt esto cambia
        if (usuario == null || !usuario.getPassword().equals(password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if ("INACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("Tu cuenta se encuentra deshabilitada. Contacta a un administrador.");
        }

        LoginResponseDTO.Usuario datos = new LoginResponseDTO.Usuario();
        datos.setId(usuario.getId());
        datos.setRun(usuario.getRun());
        datos.setNombre(usuario.getNombre());
        datos.setApellidos(usuario.getApellidos());
        datos.setEmail(usuario.getEmail());
        datos.setRol(usuario.getRol());

        LoginResponseDTO respuesta = new LoginResponseDTO();
        respuesta.setToken(jwtUtil.generarToken(usuario.getEmail(), usuario.getRol()));
        respuesta.setUsuario(datos);
        return respuesta;
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
