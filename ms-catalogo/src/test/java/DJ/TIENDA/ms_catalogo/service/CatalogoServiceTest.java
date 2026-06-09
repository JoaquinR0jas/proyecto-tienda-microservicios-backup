package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.client.InventarioClient;
import DJ.TIENDA.ms_catalogo.dto.InventarioDTO;
import DJ.TIENDA.ms_catalogo.dto.ProductoDetalleDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private CatalogoService catalogoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Test Product");
        producto.setDescripcion("A test product");
        producto.setPrecio(99.99);
    }

    @Test
    void obtenerTodos_returnsAllProducts() {
        when(productoRepository.findAll()).thenReturn(List.of(producto));

        List<Producto> resultado = catalogoService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Test Product", resultado.get(0).getNombre());
    }

    @Test
    void obtenerPorId_whenExists_returnsProduct() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = catalogoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(99.99, resultado.get().getPrecio());
    }

    @Test
    void obtenerPorId_whenNotExists_returnsEmpty() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = catalogoService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_savesAndReturnsProduct() {
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = catalogoService.crear(producto);

        assertNotNull(resultado);
        assertEquals("Test Product", resultado.getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void eliminar_whenExists_returnsTrueAndDeletes() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = catalogoService.eliminar(1L);

        assertTrue(resultado);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_whenNotExists_returnsFalse() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = catalogoService.eliminar(99L);

        assertFalse(resultado);
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void obtenerDetalleConStock_whenProductExistsAndHasStock_returnsDetailWithStock() {
        InventarioDTO inventarioDTO = new InventarioDTO();
        inventarioDTO.setCantidad(50);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioClient.obtenerStock(1L)).thenReturn(inventarioDTO);

        Optional<ProductoDetalleDTO> resultado = catalogoService.obtenerDetalleConStock(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Test Product", resultado.get().getNombre());
        assertEquals(99.99, resultado.get().getPrecio());
        assertEquals(50, resultado.get().getStockDisponible());
    }

    @Test
    void obtenerDetalleConStock_whenProductExistsAndNoStock_returnsDetailWithZeroStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioClient.obtenerStock(1L)).thenReturn(null);

        Optional<ProductoDetalleDTO> resultado = catalogoService.obtenerDetalleConStock(1L);

        assertTrue(resultado.isPresent());
        assertEquals(0, resultado.get().getStockDisponible());
    }

    @Test
    void obtenerDetalleConStock_whenProductNotExists_returnsEmpty() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ProductoDetalleDTO> resultado = catalogoService.obtenerDetalleConStock(99L);

        assertFalse(resultado.isPresent());
    }
}
