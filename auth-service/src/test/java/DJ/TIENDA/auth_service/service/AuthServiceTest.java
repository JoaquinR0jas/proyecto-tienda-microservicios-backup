package DJ.TIENDA.auth_service.service;

import DJ.TIENDA.auth_service.client.UsuarioClient;
import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import DJ.TIENDA.auth_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombre("Test User");
        usuarioDTO.setEmail("test@example.com");
        usuarioDTO.setPassword("password123");
        usuarioDTO.setRol("CLIENTE");
    }

    @Test
    void login_whenCredentialsAreValid_returnsToken() {
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);
        when(jwtUtil.generarToken("test@example.com", "CLIENTE")).thenReturn("jwt-token");

        String token = authService.login("test@example.com", "password123");

        assertEquals("jwt-token", token);
        verify(usuarioClient).buscarPorEmail("test@example.com");
        verify(jwtUtil).generarToken("test@example.com", "CLIENTE");
    }

    @Test
    void login_whenUserNotFound_throwsException() {
        when(usuarioClient.buscarPorEmail("notfound@example.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("notfound@example.com", "password"));
        assertEquals("Credenciales inv\u00e1lidas", exception.getMessage());
    }

    @Test
    void login_whenPasswordDoesNotMatch_throwsException() {
        when(usuarioClient.buscarPorEmail("test@example.com")).thenReturn(usuarioDTO);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("test@example.com", "wrongpassword"));
        assertEquals("Credenciales inv\u00e1lidas", exception.getMessage());
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
