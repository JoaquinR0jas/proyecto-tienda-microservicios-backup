package DJ.TIENDA.auth_service.service;

import DJ.TIENDA.auth_service.client.UsuarioClient;
import DJ.TIENDA.auth_service.dto.LoginResponseDTO;
import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import DJ.TIENDA.auth_service.util.JwtUtil;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioClient usuarioClient; // Para consultar ms-usuarios via Feign

    @Autowired
    private JwtUtil jwtUtil; // Para generar y validar tokens

    @Autowired
    private PasswordEncoder passwordEncoder; // Para comparar la clave con el hash BCrypt

    // Lógica del login: verifica credenciales y genera el JWT junto con los datos de la sesion
    public LoginResponseDTO login(String email, String password) {
        UsuarioDTO usuario = buscarUsuario(email);

        // La base guarda la clave encriptada con BCrypt, por eso usamos matches()
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if ("INACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("Tu cuenta se encuentra deshabilitada. Contacta a un administrador.");
        }

        return armarRespuesta(usuario);
    }

    // Renueva el token: si el actual todavia sirve, entrega uno nuevo con otra hora de duracion
    public LoginResponseDTO renovarToken(String token) {
        io.jsonwebtoken.Claims claims;
        try {
            // Acepta tambien un token vencido hace poco, para que el usuario no
            // tenga que volver a escribir la clave apenas se le acaba la hora.
            claims = jwtUtil.obtenerClaimsAunqueEsteVencido(token);
        } catch (Exception e) {
            throw new RuntimeException("La sesión venció, vuelve a iniciar sesión");
        }

        // Despues de 7 dias vencido ya no se renueva: hay que hacer login de nuevo
        java.util.Date vencimiento = claims.getExpiration();
        long sieteDias = 7L * 24 * 60 * 60 * 1000;
        if (vencimiento == null || System.currentTimeMillis() - vencimiento.getTime() > sieteDias) {
            throw new RuntimeException("La sesión venció, vuelve a iniciar sesión");
        }

        UsuarioDTO usuario = buscarUsuario(claims.getSubject());
        if (usuario == null || "INACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("La sesión venció, vuelve a iniciar sesión");
        }

        return armarRespuesta(usuario);
    }

    // Busca el usuario en ms-usuarios. Si no existe, Feign lanza 404 y devolvemos null
    private UsuarioDTO buscarUsuario(String email) {
        try {
            return usuarioClient.buscarPorEmail(email);
        } catch (FeignException.NotFound e) {
            return null;
        }
    }

    // Arma la respuesta que recibe el frontend: token + datos basicos del usuario
    private LoginResponseDTO armarRespuesta(UsuarioDTO usuario) {
        LoginResponseDTO.Usuario datos = new LoginResponseDTO.Usuario();
        datos.setId(usuario.getId());
        datos.setRun(usuario.getRun());
        datos.setNombre(usuario.getNombre());
        datos.setApellidos(usuario.getApellidos());
        datos.setEmail(usuario.getEmail());
        datos.setRol(usuario.getRol());

        LoginResponseDTO respuesta = new LoginResponseDTO();
        respuesta.setToken(jwtUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol()));
        respuesta.setUsuario(datos);
        return respuesta;
    }

    // Lógica de validación: verifica si un token JWT es válido
    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }

    // Extrae el email del token
    public String obtenerEmail(String token) {
        return jwtUtil.obtenerEmail(token);
    }

    // Extrae el rol del token
    public String obtenerRol(String token) {
        return jwtUtil.obtenerRol(token);
    }
}
