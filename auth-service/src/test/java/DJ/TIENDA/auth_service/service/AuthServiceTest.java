package DJ.TIENDA.auth_service.service;

import DJ.TIENDA.auth_service.client.UsuarioClient;
import DJ.TIENDA.auth_service.dto.LoginResponseDTO;
import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import DJ.TIENDA.auth_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("Test User");
        usuarioDTO.setEmail("test@example.com");
        // El hash real lo guarda ms-usuarios; aca basta con simular la comparacion
        usuarioDTO.setPassword("$2a$10$hashdeprueba");
        usuarioDTO.setRol("CLIENTE");
        usuarioDTO.setEstado("ACTIVO");
    }

    @Test
    void login_whenCredentialsAreValid_returnsTokenAndUserData() {
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);
        when(passwordEncoder.matches("password123", usuarioDTO.getPassword())).thenReturn(true);
        when(jwtUtil.generarToken(1L, "test@example.com", "CLIENTE")).thenReturn("jwt-token");

        LoginResponseDTO respuesta = authService.login("test@example.com", "password123");

        assertEquals("jwt-token", respuesta.getToken());
        assertEquals("Bearer", respuesta.getTipo());
        assertEquals(1L, respuesta.getUsuario().getId());
        assertEquals("CLIENTE", respuesta.getUsuario().getRol());
        verify(usuarioClient).buscarPorEmail("test@example.com");
        verify(jwtUtil).generarToken(1L, "test@example.com", "CLIENTE");
    }

    @Test
    void login_whenUserIsInactive_throwsException() {
        usuarioDTO.setEstado("INACTIVO");
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);
        when(passwordEncoder.matches("password123", usuarioDTO.getPassword())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("test@example.com", "password123"));
        assertTrue(exception.getMessage().contains("deshabilitada"));
        verify(jwtUtil, never()).generarToken(any(), any(), any());
    }

    @Test
    void login_whenUserNotFound_throwsException() {
        when(usuarioClient.buscarPorEmail("notfound@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("notfound@example.com", "password"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void login_whenPasswordDoesNotMatch_throwsException() {
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);
        when(passwordEncoder.matches("wrongpassword", usuarioDTO.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("test@example.com", "wrongpassword"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    void renovarToken_whenTokenIsStillValid_returnsNewToken() {
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 60000));
        when(claims.getSubject()).thenReturn("test@example.com");
        when(jwtUtil.obtenerClaimsAunqueEsteVencido("token-viejo")).thenReturn(claims);
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);
        when(jwtUtil.generarToken(1L, "test@example.com", "CLIENTE")).thenReturn("token-nuevo");

        LoginResponseDTO respuesta = authService.renovarToken("token-viejo");

        assertEquals("token-nuevo", respuesta.getToken());
        assertEquals(1L, respuesta.getUsuario().getId());
    }

    @Test
    void renovarToken_whenTokenExpiredLongAgo_throwsException() {
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 8L * 24 * 60 * 60 * 1000));
        when(jwtUtil.obtenerClaimsAunqueEsteVencido("token-viejo")).thenReturn(claims);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.renovarToken("token-viejo"));
        assertTrue(exception.getMessage().contains("venció"));
    }

    @Test
    void validarToken_whenTokenIsValid_returnsTrue() {
        when(jwtUtil.validarToken("valid-token")).thenReturn(true);

        assertTrue(authService.validarToken("valid-token"));
    }

    @Test
    void validarToken_whenTokenIsInvalid_returnsFalse() {
        when(jwtUtil.validarToken("invalid-token")).thenReturn(false);

        assertFalse(authService.validarToken("invalid-token"));
    }

    @Test
    void obtenerEmail_returnsEmailFromToken() {
        when(jwtUtil.obtenerEmail("token")).thenReturn("test@example.com");

        String email = authService.obtenerEmail("token");

        assertEquals("test@example.com", email);
    }

    @Test
    void obtenerRol_returnsRolFromToken() {
        when(jwtUtil.obtenerRol("token")).thenReturn("ADMIN");

        String rol = authService.obtenerRol("token");

        assertEquals("ADMIN", rol);
    }
}
