package DJ.TIENDA.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private static final ObjectMapper om = new ObjectMapper();

    @Test
    void listarTodos_returnsAllUsers() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("pass");
        usuario.setRol(Usuario.Rol.CLIENTE);

        when(usuarioService.obtenerTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void obtenerPorId_whenExists_returnsUser() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test");
        usuario.setEmail("test@example.com");

        when(usuarioService.obtenerPorId(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test"));
    }

    @Test
    void obtenerPorId_whenNotExists_returns404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorEmail_whenExists_returnsUser() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");

        when(usuarioService.obtenerPorEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/usuarios/buscar").param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void obtenerPorEmail_whenNotExists_returns404() throws Exception {
        when(usuarioService.obtenerPorEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/usuarios/buscar").param("email", "notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_whenValid_returns201() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nuevo");
        usuario.setEmail("nuevo@example.com");
        usuario.setPassword("pass");

        Usuario creado = new Usuario();
        creado.setId(1L);
        creado.setNombre("Nuevo");
        creado.setEmail("nuevo@example.com");
        creado.setPassword("pass");
        creado.setRol(Usuario.Rol.CLIENTE);

        when(usuarioService.crear(any(Usuario.class))).thenReturn(creado);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_whenEmailAlreadyExists_returns409() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Nuevo");
        usuario.setEmail("existing@example.com");
        usuario.setPassword("pass");

        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con el email: existing@example.com"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(usuario)))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizar_whenExists_returns200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Updated");
        usuario.setEmail("test@example.com");
        usuario.setPassword("pass");

        Usuario actualizado = new Usuario();
        actualizado.setId(1L);
        actualizado.setNombre("Updated");

        when(usuarioService.actualizar(eq(1L), any(Usuario.class))).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_whenNotExists_returns404() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Updated");
        usuario.setEmail("test@example.com");
        usuario.setPassword("pass");

        when(usuarioService.actualizar(eq(99L), any(Usuario.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_whenExists_returns200() throws Exception {
        when(usuarioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(usuarioService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/usuarios/99"))
                .andExpect(status().isNotFound());
    }
}
