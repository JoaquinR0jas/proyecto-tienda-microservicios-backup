package DJ.TIENDA.ms_envios.service;

import DJ.TIENDA.ms_envios.client.UsuarioClient;
import DJ.TIENDA.ms_envios.dto.EnvioResponseDTO;
import DJ.TIENDA.ms_envios.dto.UsuarioDTO;
import DJ.TIENDA.ms_envios.model.Envio;
import DJ.TIENDA.ms_envios.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @InjectMocks
    private EnvioService envioService;

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Test
    void crearEnvio_DebeCrear_CuandoDatosValidos() {
        Long pedidoId = 1L;
        Long usuarioId = 10L;
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(usuarioId);
        usuario.setDireccion("Calle 123");
        Envio envioGuardado = new Envio();
        envioGuardado.setId(100L);
        envioGuardado.setPedidoId(pedidoId);
        envioGuardado.setUsuarioId(usuarioId);
        envioGuardado.setDireccionEntrega("Calle 123");

        when(envioRepository.findByPedidoId(pedidoId)).thenReturn(Optional.empty());
        when(usuarioClient.obtenerUsuario(usuarioId)).thenReturn(usuario);
        when(envioRepository.save(any(Envio.class))).thenReturn(envioGuardado);

        EnvioResponseDTO resultado = envioService.crearEnvio(pedidoId, usuarioId);

        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getPedidoId());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals("Calle 123", resultado.getDireccionEntrega());
    }

    @Test
    void crearEnvio_DebeLanzarExcepcion_CuandoYaExiste() {
        when(envioRepository.findByPedidoId(1L)).thenReturn(Optional.of(new Envio()));

        assertThrows(IllegalArgumentException.class,
                () -> envioService.crearEnvio(1L, 10L));
    }

    @Test
    void crearEnvio_DebeLanzarExcepcion_CuandoUsuarioSinDireccion() {
        when(envioRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(10L);
        usuario.setDireccion(null);
        when(usuarioClient.obtenerUsuario(10L)).thenReturn(usuario);

        assertThrows(IllegalArgumentException.class,
                () -> envioService.crearEnvio(1L, 10L));
    }

    @Test
    void actualizarEstado_DebeActualizarEstado() {
        Long envioId = 100L;
        Envio envio = new Envio();
        envio.setId(envioId);
        envio.setEstado(Envio.Estado.PENDIENTE);

        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        EnvioResponseDTO resultado = envioService.actualizarEstado(envioId, Envio.Estado.EN_CAMINO);

        assertNotNull(resultado);
        assertEquals("EN_CAMINO", resultado.getEstado());
    }

    @Test
    void actualizarEstado_DebeLanzarExcepcion_CuandoNoExiste() {
        when(envioRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> envioService.actualizarEstado(999L, Envio.Estado.ENTREGADO));
    }

    @Test
    void obtenerPorPedido_DebeRetornarEnvio() {
        Long pedidoId = 1L;
        Envio envio = new Envio();
        envio.setId(100L);
        envio.setPedidoId(pedidoId);

        when(envioRepository.findByPedidoId(pedidoId)).thenReturn(Optional.of(envio));

        Optional<EnvioResponseDTO> resultado = envioService.obtenerPorPedido(pedidoId);

        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getPedidoId());
    }

    @Test
    void obtenerPorPedido_DebeRetornarEmpty_CuandoNoExiste() {
        when(envioRepository.findByPedidoId(999L)).thenReturn(Optional.empty());

        Optional<EnvioResponseDTO> resultado = envioService.obtenerPorPedido(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorUsuario_DebeRetornarLista() {
        Long usuarioId = 10L;
        Envio envio = new Envio();
        envio.setId(100L);
        envio.setUsuarioId(usuarioId);

        when(envioRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(envio));

        List<EnvioResponseDTO> resultados = envioService.obtenerPorUsuario(usuarioId);

        assertEquals(1, resultados.size());
        assertEquals(usuarioId, resultados.get(0).getUsuarioId());
    }

    @Test
    void obtenerPorUsuario_DebeRetornarListaVacia_CuandoNoHay() {
        when(envioRepository.findByUsuarioId(999L)).thenReturn(List.of());

        List<EnvioResponseDTO> resultados = envioService.obtenerPorUsuario(999L);

        assertTrue(resultados.isEmpty());
    }
}
