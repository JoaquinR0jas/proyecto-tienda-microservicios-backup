package DJ.TIENDA.ms_pedidos.service;

import DJ.TIENDA.ms_pedidos.client.CarritoClient;
import DJ.TIENDA.ms_pedidos.dto.CarritoDTO;
import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.model.Pedido;
import DJ.TIENDA.ms_pedidos.repository.PedidoRepository;
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
class PedidoServiceTest {

    @InjectMocks
    private PedidoService pedidoService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CarritoClient carritoClient;

    @Test
    void crearPedido_DebeCrear_CuandoCarritoConfirmado() {
        Long usuarioId = 1L;
        Long carritoId = 100L;
        CarritoDTO carrito = new CarritoDTO();
        carrito.setCarritoId(carritoId);
        carrito.setEstado("CONFIRMADO");
        carrito.setTotal(199.98);
        CarritoDTO.ItemDTO itemDTO = new CarritoDTO.ItemDTO();
        itemDTO.setProductoId(10L);
        itemDTO.setCantidad(2);
        itemDTO.setPrecioUnitario(99.99);
        carrito.setItems(List.of(itemDTO));

        when(carritoClient.obtenerCarritoPorId(carritoId)).thenReturn(carrito);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArgument(0));

        PedidoResponseDTO resultado = pedidoService.crearPedido(usuarioId, carritoId);

        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(carritoId, resultado.getCarritoId());
        assertEquals(199.98, resultado.getTotal());
        assertEquals(1, resultado.getItems().size());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_DebeLanzarExcepcion_CuandoCarritoNulo() {
        when(carritoClient.obtenerCarritoPorId(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(1L, 999L));
    }

    @Test
    void crearPedido_DebeLanzarExcepcion_CuandoCarritoEstaVacio() {
        CarritoDTO carrito = new CarritoDTO();
        carrito.setItems(List.of());

        when(carritoClient.obtenerCarritoPorId(100L)).thenReturn(carrito);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(1L, 100L));
    }

    @Test
    void crearPedido_DebeLanzarExcepcion_CuandoCarritoNoConfirmado() {
        CarritoDTO carrito = new CarritoDTO();
        carrito.setItems(List.of(new CarritoDTO.ItemDTO()));
        carrito.setEstado("ACTIVO");

        when(carritoClient.obtenerCarritoPorId(100L)).thenReturn(carrito);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(1L, 100L));
    }

    @Test
    void obtenerPedidosPorUsuario_DebeRetornarLista() {
        Long usuarioId = 1L;
        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setUsuarioId(usuarioId);
        pedido.setItems(List.of());

        when(pedidoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultados = pedidoService.obtenerPedidosPorUsuario(usuarioId);

        assertEquals(1, resultados.size());
        assertEquals(usuarioId, resultados.get(0).getUsuarioId());
    }

    @Test
    void obtenerPedidosPorUsuario_DebeRetornarListaVacia_CuandoNoHay() {
        when(pedidoRepository.findByUsuarioId(999L)).thenReturn(List.of());

        List<PedidoResponseDTO> resultados = pedidoService.obtenerPedidosPorUsuario(999L);

        assertTrue(resultados.isEmpty());
    }

    @Test
    void obtenerPorId_DebeRetornarPedido() {
        Long pedidoId = 100L;
        Pedido pedido = new Pedido();
        pedido.setId(pedidoId);
        pedido.setItems(List.of());

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        Optional<PedidoResponseDTO> resultado = pedidoService.obtenerPorId(pedidoId);

        assertTrue(resultado.isPresent());
        assertEquals(pedidoId, resultado.get().getPedidoId());
    }

    @Test
    void obtenerPorId_DebeRetornarEmpty_CuandoNoExiste() {
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<PedidoResponseDTO> resultado = pedidoService.obtenerPorId(999L);

        assertTrue(resultado.isEmpty());
    }
}
