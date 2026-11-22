package DJ.TIENDA.ms_usuarios.service;

import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRun("112223339");
        usuario.setNombre("Test User");
        usuario.setApellidos("Prueba");
        usuario.setEmail("test@example.com");
        usuario.setPassword("pass123");
        usuario.setRegionId(1L);
        usuario.setComunaId(1L);
        usuario.setDireccion("Calle test 123");
        usuario.setRol(Usuario.Rol.CLIENTE);
        // Simulamos BCrypt: el service tiene que guardar el hash, no la clave escrita
        lenient().when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
    }

    @Test
    void obtenerTodos_returnsAllUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> resultado = usuarioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("test@example.com", resultado.get(0).getEmail());
    }

    @Test
    void obtenerPorId_whenUserExists_returnsUser() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Test User", resultado.get().getNombre());
    }

    @Test
    void obtenerPorId_whenUserDoesNotExist_returnsEmpty() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void obtenerPorEmail_whenUserExists_returnsUser() {
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.obtenerPorEmail("test@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("test@example.com", resultado.get().getEmail());
    }

    @Test
    void obtenerPorEmail_whenUserDoesNotExist_returnsEmpty() {
        when(usuarioRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorEmail("notfound@example.com");

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_whenEmailIsUnique_savesAndReturnsUser() {
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.crear(usuario);

        assertNotNull(resultado);
        assertEquals("test@example.com", resultado.getEmail());
        verify(usuarioRepository).existsByEmail("test@example.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void crear_guardaLaContrasenaEncriptada() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.crear(usuario);

        // La clave original "pass123" no debe quedar guardada tal cual
        assertEquals("$2a$10$hash", resultado.getPassword());
        assertNotEquals("pass123", resultado.getPassword());
    }

    @Test
    void crear_whenEmailAlreadyExists_throwsException() {
        when(usuarioRepository.existsByEmail("test@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));
        assertTrue(exception.getMessage().contains("Ya existe un usuario"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_whenRunAlreadyExists_throwsException() {
        when(usuarioRepository.existsByRun("112223339")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(usuario));
        assertTrue(exception.getMessage().contains("RUN"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crear_normalizesRunWithDotsAndDash() {
        usuario.setRun("11.222.333-9");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.crear(usuario);

        assertEquals("112223339", resultado.getRun());
    }

    @Test
    void actualizar_changesRolAndEstado() {
        Usuario datosActualizados = new Usuario();
        datosActualizados.setEmail("test@example.com");
        datosActualizados.setRol(Usuario.Rol.VENDEDOR);
        datosActualizados.setEstado(Usuario.Estado.INACTIVO);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = usuarioService.actualizar(1L, datosActualizados).orElseThrow();

        assertEquals(Usuario.Rol.VENDEDOR, resultado.getRol());
        assertEquals(Usuario.Estado.INACTIVO, resultado.getEstado());
    }

    @Test
    void actualizar_whenUserExistsAndEmailUnchanged_updatesUser() {
        Usuario datosActualizados = new Usuario();
        datosActualizados.setNombre("Updated Name");
        datosActualizados.setEmail("test@example.com");
        datosActualizados.setPassword("newpass");
        datosActualizados.setDireccion("New dir");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<Usuario> resultado = usuarioService.actualizar(1L, datosActualizados);

        assertTrue(resultado.isPresent());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void actualizar_whenUserDoesNotExist_returnsEmpty() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.actualizar(99L, new Usuario());

        assertFalse(resultado.isPresent());
    }

    @Test
    void actualizar_whenEmailAlreadyInUse_throwsException() {
        Usuario datosActualizados = new Usuario();
        datosActualizados.setEmail("other@example.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("other@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.actualizar(1L, datosActualizados));
        assertTrue(exception.getMessage().contains("El email ya est\u00e1 en uso"));
    }

    @Test
    void eliminar_whenUserExists_returnsTrueAndDeletes() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        boolean resultado = usuarioService.eliminar(1L);

        assertTrue(resultado);
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_whenUserDoesNotExist_returnsFalse() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        boolean resultado = usuarioService.eliminar(99L);

        assertFalse(resultado);
        verify(usuarioRepository, never()).deleteById(any());
    }
}
