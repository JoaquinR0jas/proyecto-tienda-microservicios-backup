package DJ.TIENDA.ms_inventario.service;

import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.repository.InventarioRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(10L);
        inventario.setCantidad(100);
    }

    @Test
    void obtenerTodo_returnsAllStock() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.obtenerTodo();

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getProductoId());
    }

    @Test
    void obtenerPorProductoId_whenExists_returnsStock() {
        when(inventarioRepository.findByProductoId(10L)).thenReturn(List.of(inventario));

        Optional<Inventario> resultado = inventarioService.obtenerPorProductoId(10L);

        assertTrue(resultado.isPresent());
        assertEquals(100, resultado.get().getCantidad());
    }

    @Test
    void obtenerPorProductoId_whenNotExists_returnsEmpty() {
        when(inventarioRepository.findByProductoId(99L)).thenReturn(List.of());

        Optional<Inventario> resultado = inventarioService.obtenerPorProductoId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void guardar_savesAndReturnsInventario() {
        when(inventarioRepository.findByProductoId(inventario.getProductoId())).thenReturn(List.of());
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.guardar(inventario);

        assertNotNull(resultado);
        assertEquals(100, resultado.getCantidad());
        verify(inventarioRepository).findByProductoId(inventario.getProductoId());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void eliminar_whenExists_returnsTrueAndDeletes() {
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        boolean resultado = inventarioService.eliminar(1L);

        assertTrue(resultado);
        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void eliminar_whenNotExists_returnsFalse() {
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        boolean resultado = inventarioService.eliminar(99L);

        assertFalse(resultado);
        verify(inventarioRepository, never()).deleteById(any());
    }
}
