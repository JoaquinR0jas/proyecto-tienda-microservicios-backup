package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.model.Categoria;
import DJ.TIENDA.ms_catalogo.repository.CategoriaRepository;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Poleras");
    }

    @Test
    void crear_whenNameIsUnique_saves() {
        when(categoriaRepository.existsByNombreIgnoreCase("Poleras")).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        assertEquals("Poleras", categoriaService.crear(categoria).getNombre());
    }

    @Test
    void crear_whenNameAlreadyExists_throwsException() {
        when(categoriaRepository.existsByNombreIgnoreCase("Poleras")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> categoriaService.crear(categoria));
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void actualizar_whenNotExists_returnsEmpty() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(categoriaService.actualizar(99L, categoria).isPresent());
    }

    @Test
    void eliminar_whenHasNoProducts_deletes() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(false);

        assertTrue(categoriaService.eliminar(1L));
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void eliminar_whenHasProducts_throwsAndDoesNotDelete() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);
        when(productoRepository.existsByCategoriaId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> categoriaService.eliminar(1L));
        verify(categoriaRepository, never()).deleteById(any());
    }

    @Test
    void eliminar_whenNotExists_returnsFalse() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        assertFalse(categoriaService.eliminar(99L));
    }
}
