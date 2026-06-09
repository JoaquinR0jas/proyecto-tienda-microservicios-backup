package DJ.TIENDA.auth_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import DJ.TIENDA.auth_service.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private static final ObjectMapper om = new ObjectMapper();

    @Test
    void login_whenCredentialsAreValid_returnsToken() throws Exception {
        when(authService.login("test@example.com", "password123")).thenReturn("jwt-token-value");

        Map<String, String> credenciales = Map.of("email", "test@example.com", "password", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(credenciales)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-value"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void login_whenCredentialsAreInvalid_returnsUnauthorized() throws Exception {
        when(authService.login("test@example.com", "wrong")).thenThrow(new RuntimeException("Credenciales inv\u00e1lidas"));

        Map<String, String> credenciales = Map.of("email", "test@example.com", "password", "wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validate_whenTokenIsValid_returnsOkWithDetails() throws Exception {
        when(authService.validarToken("valid-token")).thenReturn(true);
        when(authService.obtenerEmail("valid-token")).thenReturn("test@example.com");
        when(authService.obtenerRol("valid-token")).thenReturn("CLIENTE");

        mockMvc.perform(get("/auth/validate").param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"))
                .andExpect(jsonPath("$.valido").value("true"));
    }

    @Test
    void validate_whenTokenIsInvalid_returnsUnauthorized() throws Exception {
        when(authService.validarToken("invalid-token")).thenReturn(false);

        mockMvc.perform(get("/auth/validate").param("token", "invalid-token"))
                .andExpect(status().isUnauthorized());
    }
}
