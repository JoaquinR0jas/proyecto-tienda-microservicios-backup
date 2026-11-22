package DJ.TIENDA.ms_contenido.service;

import DJ.TIENDA.ms_contenido.model.Articulo;
import DJ.TIENDA.ms_contenido.repository.ArticuloRepository;
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
class ArticuloServiceTest {

    @Mock
    private ArticuloRepository articuloRepository;

    @InjectMocks
    private ArticuloService articuloService;

    private Articulo articulo;

    @BeforeEach
    void setUp() {
        articulo = new Articulo();
        articulo.setId(1L);
        articulo.setSlug("detalle-1");
        articulo.setTitulo("FreshFits abre sus puertas");
        articulo.setResumen("Resumen");
        articulo.setContenido("Contenido");
    }

    @Test
    void obtenerArticulos_forStore_returnsOnlyPublished() {
        when(articuloRepository.findByEstadoOrderByFechaDesc(Articulo.Estado.PUBLICADO)).thenReturn(List.of(articulo));

        assertEquals(1, articuloService.obtenerArticulos(false).size());
        verify(articuloRepository, never()).findAllByOrderByFechaDesc();
    }

    @Test
    void obtenerArticulos_forAdmin_returnsAll() {
        when(articuloRepository.findAllByOrderByFechaDesc()).thenReturn(List.of(articulo));

        assertEquals(1, articuloService.obtenerArticulos(true).size());
    }

    @Test
    void crear_whenSlugIsUnique_saves() {
        when(articuloRepository.existsBySlug("detalle-1")).thenReturn(false);
        when(articuloRepository.save(articulo)).thenReturn(articulo);

        assertEquals("detalle-1", articuloService.crear(articulo).getSlug());
    }

    @Test
    void crear_whenSlugAlreadyExists_throwsException() {
        when(articuloRepository.existsBySlug("detalle-1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> articuloService.crear(articulo));
        verify(articuloRepository, never()).save(any());
    }

    @Test
    void actualizar_whenNotExists_returnsEmpty() {
        when(articuloRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(articuloService.actualizar(99L, articulo).isPresent());
    }

    @Test
    void eliminar_whenExists_deletes() {
        when(articuloRepository.existsById(1L)).thenReturn(true);

        assertTrue(articuloService.eliminar(1L));
        verify(articuloRepository).deleteById(1L);
    }
}
