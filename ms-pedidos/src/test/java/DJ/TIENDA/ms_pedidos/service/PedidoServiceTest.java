package DJ.TIENDA.ms_pedidos.service;

import DJ.TIENDA.ms_pedidos.client.CatalogoClient;
import DJ.TIENDA.ms_pedidos.client.PagoClient;
import DJ.TIENDA.ms_pedidos.dto.*;
import DJ.TIENDA.ms_pedidos.model.Pedido;
import DJ.TIENDA.ms_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private PagoClient pagoClient;

    @InjectMocks
    private PedidoService pedidoService;

    private CrearPedidoDTO solicitud;
    private ProductoDTO producto;

    @BeforeEach
    void setUp() {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setRun("112223339");
        cliente.setNombre("Camila");
        cliente.setApellidos("Soto Perez");
        cliente.setEmail("camila@gmail.com");

        EntregaDTO entrega = new EntregaDTO();
        entrega.setTipo("DESPACHO");
        entrega.setCalle("Los Aromos 789");
        entrega.setRegionId(2L);
        entrega.setComunaId(4L);

        CrearPedidoDTO.Item item = new CrearPedidoDTO.Item();
        item.setProductoId(1L);
        item.setCantidad(2);

        solicitud = new CrearPedidoDTO();
        solicitud.setUsuarioId(3L);
        solicitud.setCliente(cliente);
        solicitud.setEntrega(entrega);
        solicitud.setMedioPago("debito");
        solicitud.setItems(List.of(item));

        producto = new ProductoDTO();
        producto.setId(1L);
        producto.setCodigo("FF-002");
        producto.setNombre("Poleron Cloud Hoodie");
        producto.setPrecioFinal(23992.0);
        producto.setStock(20);
        producto.setEstado("ACTIVO");
    }

    private PagoDTO pagoAprobado(long folio) {
        PagoDTO.Boleta boleta = new PagoDTO.Boleta();
        boleta.setFolio(folio);
        PagoDTO pago = new PagoDTO();
        pago.setEstado("APROBADO");
        pago.setBoleta(boleta);
        return pago;
    }

    @Test
    void crearPedido_whenPaymentApproved_isPaidWithBoletaAndDiscountsStock() {
        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);
        when(pagoClient.procesarPago(any(PagoDTO.class))).thenReturn(pagoAprobado(15L));

        PedidoResponseDTO resultado = pedidoService.crearPedido(solicitud);

        assertEquals("PAGADO", resultado.getEstado());
        assertEquals(15L, resultado.getFolioBoleta());
        assertEquals(47984.0, resultado.getSubtotal());
        assertEquals(3990.0, resultado.getCostoEnvio()); // bajo $50.000 se cobra despacho
        assertEquals(51974.0, resultado.getTotal());
        assertEquals("Poleron Cloud Hoodie", resultado.getItems().get(0).getNombre());
        verify(catalogoClient).descontarStock(anyList());
    }

    @Test
    void crearPedido_whenSubtotalOver50000_shippingIsFree() {
        solicitud.getItems().get(0).setCantidad(3);
        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);
        when(pagoClient.procesarPago(any(PagoDTO.class))).thenReturn(pagoAprobado(1L));

        PedidoResponseDTO resultado = pedidoService.crearPedido(solicitud);

        assertEquals(0.0, resultado.getCostoEnvio());
    }

    @Test
    void crearPedido_whenPickupInStore_shippingIsFreeAndAddressIgnored() {
        solicitud.getEntrega().setTipo("RETIRO");
        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);
        when(pagoClient.procesarPago(any(PagoDTO.class))).thenReturn(pagoAprobado(1L));

        PedidoResponseDTO resultado = pedidoService.crearPedido(solicitud);

        assertEquals(0.0, resultado.getCostoEnvio());
        assertNull(resultado.getEntrega().getCalle());
    }

    @Test
    void crearPedido_whenPaymentRejected_isRejectedWithReasonAndKeepsStock() {
        solicitud.setSimularRechazo(true);
        PagoDTO rechazado = new PagoDTO();
        rechazado.setEstado("RECHAZADO");
        rechazado.setDetalle("El medio de pago fue rechazado por el banco");

        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);
        when(pagoClient.procesarPago(any(PagoDTO.class))).thenReturn(rechazado);

        PedidoResponseDTO resultado = pedidoService.crearPedido(solicitud);

        assertEquals("RECHAZADO", resultado.getEstado());
        assertEquals("El medio de pago fue rechazado por el banco", resultado.getMotivoRechazo());
        assertNull(resultado.getFolioBoleta());
        verify(catalogoClient, never()).descontarStock(anyList());
    }

    @Test
    void crearPedido_whenNotEnoughStock_isRejectedWithoutCharging() {
        producto.setStock(1);
        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);

        PedidoResponseDTO resultado = pedidoService.crearPedido(solicitud);

        assertEquals("RECHAZADO", resultado.getEstado());
        assertTrue(resultado.getMotivoRechazo().contains("No hay stock suficiente"));
        verify(pagoClient, never()).procesarPago(any());
        verify(catalogoClient, never()).descontarStock(anyList());
    }

    @Test
    void crearPedido_whenDeliveryWithoutAddress_throwsException() {
        solicitud.getEntrega().setCalle("");

        assertThrows(IllegalArgumentException.class, () -> pedidoService.crearPedido(solicitud));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crearPedido_whenProductInactive_throwsException() {
        producto.setEstado("INACTIVO");
        when(catalogoClient.obtenerProducto(1L)).thenReturn(producto);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.crearPedido(solicitud));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void obtenerPedidosPorUsuario_returnsUserOrders() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setUsuarioId(3L);
        pedido.setClienteNombre("Camila");
        pedido.setSubtotal(1000.0);
        pedido.setCostoEnvio(0.0);
        pedido.setTotal(1000.0);

        when(pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(3L)).thenReturn(List.of(pedido));

        List<PedidoResponseDTO> resultado = pedidoService.obtenerPedidosPorUsuario(3L);

        assertEquals(1, resultado.size());
        assertEquals("Camila", resultado.get(0).getCliente().getNombre());
    }

    @Test
    void obtenerPorId_whenNotExists_returnsEmpty() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(pedidoService.obtenerPorId(99L).isPresent());
    }
}
