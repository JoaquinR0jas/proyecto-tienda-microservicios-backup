package DJ.TIENDA.ms_carrito.service;

import DJ.TIENDA.ms_carrito.client.CatalogoClient;
import DJ.TIENDA.ms_carrito.client.InventarioClient;
import DJ.TIENDA.ms_carrito.dto.CarritoResponseDTO;
import DJ.TIENDA.ms_carrito.dto.ProductoDTO;
import DJ.TIENDA.ms_carrito.dto.StockDTO;
import DJ.TIENDA.ms_carrito.model.Carrito;
import DJ.TIENDA.ms_carrito.model.CarritoItem;
import DJ.TIENDA.ms_carrito.repository.CarritoItemRepository;
import DJ.TIENDA.ms_carrito.repository.CarritoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @InjectMocks
    private CarritoService carritoService;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private InventarioClient inventarioClient;

    @Test
    void agregarProducto_DebeAgregarNuevoItem_CuandoStockSuficienteYProductoExiste() {
        Long usuarioId = 1L;
        Long productoId = 10L;
        Integer cantidad = 2;
        StockDTO stock = new StockDTO();
        stock.setCantidad(10);
        ProductoDTO producto = new ProductoDTO();
        producto.setId(productoId);
        producto.setPrecio(99.99);
        Carrito carrito = new Carrito();
        carrito.setId(100L);
        carrito.setUsuarioId(usuarioId);
        carrito.setItems(new ArrayList<>());

        when(inventarioClient.obtenerStock(productoId)).thenReturn(stock);
        when(catalogoClient.obtenerProducto(productoId)).thenReturn(producto);
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId))
                .thenReturn(Optional.empty());
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.agregarProducto(usuarioId, productoId, cantidad);

        assertNotNull(resultado);
        assertEquals(carrito.getId(), resultado.getCarritoId());
        verify(carritoItemRepository).save(any(CarritoItem.class));
    }

    @Test
    void agregarProducto_DebeAcumularCantidad_CuandoItemYaExisteEnCarrito() {
        Long usuarioId = 1L;
        Long productoId = 10L;
        Integer cantidad = 3;
        StockDTO stock = new StockDTO();
        stock.setCantidad(10);
        ProductoDTO producto = new ProductoDTO();
        producto.setId(productoId);
        producto.setPrecio(50.0);
        Carrito carrito = new Carrito();
        carrito.setId(100L);
        carrito.setUsuarioId(usuarioId);
        carrito.setItems(new ArrayList<>());
        CarritoItem itemExistente = new CarritoItem();
        itemExistente.setId(1L);
        itemExistente.setCarrito(carrito);
        itemExistente.setProductoId(productoId);
        itemExistente.setCantidad(2);
        itemExistente.setPrecioUnitario(50.0);
        carrito.getItems().add(itemExistente);

        when(inventarioClient.obtenerStock(productoId)).thenReturn(stock);
        when(catalogoClient.obtenerProducto(productoId)).thenReturn(producto);
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoItemRepository.findByCarritoIdAndProductoId(carrito.getId(), productoId))
                .thenReturn(Optional.of(itemExistente));
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.agregarProducto(usuarioId, productoId, cantidad);

        assertNotNull(resultado);
        assertEquals(5, itemExistente.getCantidad());
        verify(carritoItemRepository).save(itemExistente);
    }

    @Test
    void agregarProducto_DebeLanzarExcepcion_CuandoStockInsuficiente() {
        StockDTO stock = new StockDTO();
        stock.setCantidad(1);
        when(inventarioClient.obtenerStock(10L)).thenReturn(stock);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 10L, 5));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
    }

    @Test
    void agregarProducto_DebeLanzarExcepcion_CuandoProductoNoExiste() {
        StockDTO stock = new StockDTO();
        stock.setCantidad(10);
        when(inventarioClient.obtenerStock(10L)).thenReturn(stock);
        when(catalogoClient.obtenerProducto(10L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProducto(1L, 10L, 2));
        assertTrue(ex.getMessage().contains("Producto no encontrado"));
    }

    @Test
    void eliminarItem_DebeEliminarItem_CuandoCarritoExiste() {
        Long carritoId = 100L;
        Long itemId = 1L;
        Carrito carrito = new Carrito();
        carrito.setId(carritoId);
        carrito.setItems(new ArrayList<>());
        CarritoItem item = new CarritoItem();
        item.setId(itemId);
        carrito.getItems().add(item);

        when(carritoRepository.findById(carritoId)).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(carrito)).thenReturn(carrito);
        when(carritoRepository.findById(carritoId)).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.eliminarItem(carritoId, itemId);

        assertNotNull(resultado);
        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    void eliminarItem_DebeLanzarExcepcion_CuandoCarritoNoExiste() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.eliminarItem(999L, 1L));
    }

    @Test
    void vaciarCarrito_DebeLimpiarItems() {
        Long usuarioId = 1L;
        Carrito carrito = new Carrito();
        carrito.setId(100L);
        carrito.setItems(new ArrayList<>());
        carrito.getItems().add(new CarritoItem());

        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.vaciarCarrito(usuarioId);

        assertNotNull(resultado);
        assertTrue(carrito.getItems().isEmpty());
        verify(carritoRepository).save(carrito);
    }

    @Test
    void cambiarEstado_DebeCambiarEstado() {
        Long carritoId = 100L;
        Carrito carrito = new Carrito();
        carrito.setId(carritoId);
        carrito.setUsuarioId(1L);
        carrito.setItems(new ArrayList<>());

        when(carritoRepository.findById(carritoId)).thenReturn(Optional.of(carrito));
        when(carritoRepository.save(carrito)).thenReturn(carrito);
        when(carritoRepository.findById(carritoId)).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.cambiarEstado(carritoId, Carrito.Estado.CONFIRMADO);

        assertNotNull(resultado);
        assertEquals(Carrito.Estado.CONFIRMADO.name(), resultado.getEstado());
    }

    @Test
    void cambiarEstado_DebeLanzarExcepcion_CuandoCarritoNoExiste() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.cambiarEstado(999L, Carrito.Estado.CONFIRMADO));
    }

    @Test
    void verCarrito_DebeRetornarCarritoActivo() {
        Long usuarioId = 1L;
        Carrito carrito = new Carrito();
        carrito.setId(100L);
        carrito.setUsuarioId(usuarioId);
        carrito.setItems(new ArrayList<>());

        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoRepository.findById(carrito.getId())).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.verCarrito(usuarioId);

        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
    }

    @Test
    void verCarritoPorId_DebeRetornarCarrito() {
        Long carritoId = 100L;
        Carrito carrito = new Carrito();
        carrito.setId(carritoId);
        carrito.setUsuarioId(1L);
        carrito.setItems(new ArrayList<>());

        when(carritoRepository.findById(carritoId)).thenReturn(Optional.of(carrito));

        CarritoResponseDTO resultado = carritoService.verCarritoPorId(carritoId);

        assertNotNull(resultado);
        assertEquals(carritoId, resultado.getCarritoId());
    }

    @Test
    void verCarritoPorId_DebeLanzarExcepcion_CuandoNoExiste() {
        when(carritoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.verCarritoPorId(999L));
    }
}
