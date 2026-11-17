package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.dto.StockItemDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.CategoriaRepository;
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
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CatalogoService catalogoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setCodigo("FF-001");
        producto.setNombre("Test Product");
        producto.setDescripcion("A test product");
        producto.setPrecio(10000.0);
        producto.setStock(10);
        producto.setStockCritico(3);
        producto.setCategoriaId(1L);
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
        assertEquals(10000.0, resultado.get().getPrecio());
    }

    @Test
    void obtenerPorId_whenNotExists_returnsEmpty() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(catalogoService.obtenerPorId(99L).isPresent());
    }

    @Test
    void precioFinal_appliesDiscount() {
        producto.setDescuento(20);

        assertEquals(8000.0, producto.getPrecioFinal());
    }

    @Test
    void obtenerCriticos_returnsOnlyProductsAtOrBelowCriticalStock() {
        Producto critico = new Producto();
        critico.setStock(2);
        critico.setStockCritico(3);

        when(productoRepository.findAll()).thenReturn(List.of(producto, critico));

        List<Producto> resultado = catalogoService.obtenerCriticos();

        assertEquals(1, resultado.size());
        assertSame(critico, resultado.get(0));
    }

    @Test
    void crear_whenCodeIsUniqueAndCategoryExists_saves() {
        when(productoRepository.existsByCodigo("FF-001")).thenReturn(false);
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto resultado = catalogoService.crear(producto);

        assertEquals("FF-001", resultado.getCodigo());
        verify(productoRepository).save(producto);
    }

    @Test
    void crear_whenCodeAlreadyExists_throwsException() {
        when(productoRepository.existsByCodigo("FF-001")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> catalogoService.crear(producto));
        assertTrue(ex.getMessage().startsWith("Ya existe"));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void crear_whenCategoryDoesNotExist_throwsException() {
        when(productoRepository.existsByCodigo("FF-001")).thenReturn(false);
        when(categoriaRepository.existsById(1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> catalogoService.crear(producto));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizar_whenNotExists_returnsEmpty() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(catalogoService.actualizar(99L, producto).isPresent());
    }

    @Test
    void eliminar_whenExists_returnsTrueAndDeletes() {
        when(productoRepository.existsById(1L)).thenReturn(true);

        assertTrue(catalogoService.eliminar(1L));
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void eliminar_whenNotExists_returnsFalse() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertFalse(catalogoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(any());
    }

    @Test
    void descontarStock_whenEnoughStock_subtractsQuantity() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        catalogoService.descontarStock(List.of(new StockItemDTO(1L, 4)));

        assertEquals(6, producto.getStock());
        verify(productoRepository).save(producto);
    }

    @Test
    void descontarStock_whenNotEnoughStock_throwsAndSavesNothing() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> catalogoService.descontarStock(List.of(new StockItemDTO(1L, 11))));
        assertTrue(ex.getMessage().contains("No hay stock suficiente"));
        assertEquals(10, producto.getStock());
        verify(productoRepository, never()).save(any());
    }
}
