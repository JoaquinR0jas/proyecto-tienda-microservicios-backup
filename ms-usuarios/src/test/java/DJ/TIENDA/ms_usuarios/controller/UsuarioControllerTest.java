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

    // usuario con todos los campos obligatorios, para pasar la validacion @Valid
    private Usuario usuarioValido(String nombre, String email) {
        Usuario usuario = new Usuario();
        usuario.setRun("112223339");
        usuario.setNombre(nombre);
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("pass");
        usuario.setRegionId(1L);
        usuario.setComunaId(1L);
        usuario.setDireccion("Calle 123");
        return usuario;
    }

    // El campo password es WRITE_ONLY, por eso Jackson no lo escribe al serializar
    // la entidad. Para los POST/PUT armamos el JSON a mano e incluimos la clave.
    private String jsonUsuario(Usuario usuario) throws Exception {
        java.util.Map<String, Object> datos = new java.util.LinkedHashMap<>();
        datos.put("run", usuario.getRun());
        datos.put("nombre", usuario.getNombre());
        datos.put("apellidos", usuario.getApellidos());
        datos.put("email", usuario.getEmail());
        datos.put("password", "pass");
        datos.put("regionId", usuario.getRegionId());
        datos.put("comunaId", usuario.getComunaId());
        datos.put("direccion", usuario.getDireccion());
        return om.writeValueAsString(datos);
    }

    @Test
    void listarTodos_returnsAllUsers() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setPassword("pass");
        usuario.setRol(Usuario.Rol.CLIENTE);

        when(usuarioService.obtenerTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios"))
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

        mockMvc.perform(get("/api/v1/usuarios/1")
                        .header("X-Usuario-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Test"));
    }

    @Test
    void obtenerPorId_whenNotExists_returns404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/99")
                        .header("X-Usuario-Rol", "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorEmail_whenExists_returnsUser() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");

        when(usuarioService.obtenerPorEmail("test@example.com")).thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios/buscar").param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void obtenerPorEmail_whenNotExists_returns404() throws Exception {
        when(usuarioService.obtenerPorEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/usuarios/buscar").param("email", "notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_whenValid_returns201() throws Exception {
        Usuario usuario = usuarioValido("Nuevo", "nuevo@example.com");

        Usuario creado = new Usuario();
        creado.setId(1L);
        creado.setNombre("Nuevo");
        creado.setEmail("nuevo@example.com");
        creado.setPassword("pass");
        creado.setRol(Usuario.Rol.CLIENTE);

        when(usuarioService.crear(any(Usuario.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_whenEmailAlreadyExists_returns409() throws Exception {
        Usuario usuario = usuarioValido("Nuevo", "existing@example.com");

        when(usuarioService.crear(any(Usuario.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un usuario con el email: existing@example.com"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizar_whenExists_returns200() throws Exception {
        Usuario usuario = usuarioValido("Updated", "test@example.com");

        Usuario actualizado = new Usuario();
        actualizado.setId(1L);
        actualizado.setNombre("Updated");

        when(usuarioService.actualizar(eq(1L), any(Usuario.class))).thenReturn(Optional.of(actualizado));

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar_whenNotExists_returns404() throws Exception {
        Usuario usuario = usuarioValido("Updated", "test@example.com");

        when(usuarioService.actualizar(eq(99L), any(Usuario.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/usuarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_whenExists_returns200() throws Exception {
        when(usuarioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(usuarioService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }
    @Test
    void crear_whenMissingRequiredFields_returns400() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNombre("Sin datos");
        usuario.setEmail("sin@example.com");
        usuario.setPassword("pass");

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUsuario(usuario)))
                .andExpect(status().isBadRequest());
    }
}