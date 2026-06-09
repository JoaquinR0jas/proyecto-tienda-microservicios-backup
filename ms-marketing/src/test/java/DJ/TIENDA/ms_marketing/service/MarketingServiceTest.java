package DJ.TIENDA.ms_marketing.service;

import DJ.TIENDA.ms_marketing.dto.PromocionResponseDTO;
import DJ.TIENDA.ms_marketing.model.Promocion;
import DJ.TIENDA.ms_marketing.repository.PromocionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketingServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private MarketingService marketingService;

    private Promocion promocion;
    private Promocion promocionInactiva;

    @BeforeEach
    void setUp() {
        promocion = new Promocion();
        promocion.setId(1L);
        promocion.setNombre("Black Friday");
        promocion.setDescripcion("Descuento especial");
        promocion.setDescuentoPorcentaje(50);
        promocion.setEstado(Promocion.Estado.ACTIVO);
        promocion.setFechaCreacion(LocalDateTime.now());
        promocion.setFechaExpiracion(LocalDateTime.now().plusDays(30));

        promocionInactiva = new Promocion();
        promocionInactiva.setId(2L);
        promocionInactiva.setNombre("Oferta antigua");
        promocionInactiva.setDescripcion("Ya no vigente");
        promocionInactiva.setDescuentoPorcentaje(10);
        promocionInactiva.setEstado(Promocion.Estado.INACTIVO);
        promocionInactiva.setFechaCreacion(LocalDateTime.now().minusDays(60));
        promocionInactiva.setFechaExpiracion(LocalDateTime.now().minusDays(30));
    }

    @Test
    void crearPromocion_guardaYRetornaRespuesta() {
        when(promocionRepository.save(promocion)).thenReturn(promocion);

        PromocionResponseDTO resultado = marketingService.crearPromocion(promocion);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getPromocionId());
        assertEquals("Black Friday", resultado.getNombre());
        assertEquals("Descuento especial", resultado.getDescripcion());
        assertEquals(50, resultado.getDescuentoPorcentaje());
        assertEquals("ACTIVO", resultado.getEstado());
        verify(promocionRepository).save(promocion);
    }

    @Test
    void obtenerTodas_retornaListaCompleta() {
        when(promocionRepository.findAll()).thenReturn(List.of(promocion, promocionInactiva));

        List<PromocionResponseDTO> resultado = marketingService.obtenerTodas();

        assertEquals(2, resultado.size());
        assertEquals("Black Friday", resultado.get(0).getNombre());
        assertEquals("Oferta antigua", resultado.get(1).getNombre());
        verify(promocionRepository).findAll();
    }

    @Test
    void obtenerTodas_cuandoVacio_retornaListaVacia() {
        when(promocionRepository.findAll()).thenReturn(List.of());

        List<PromocionResponseDTO> resultado = marketingService.obtenerTodas();

        assertTrue(resultado.isEmpty());
        verify(promocionRepository).findAll();
    }

    @Test
    void obtenerActivas_retornaSoloActivas() {
        when(promocionRepository.findByEstado(Promocion.Estado.ACTIVO)).thenReturn(List.of(promocion));

        List<PromocionResponseDTO> resultado = marketingService.obtenerActivas();

        assertEquals(1, resultado.size());
        assertEquals("Black Friday", resultado.get(0).getNombre());
        verify(promocionRepository).findByEstado(Promocion.Estado.ACTIVO);
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaPromocion() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));

        Optional<PromocionResponseDTO> resultado = marketingService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Black Friday", resultado.get().getNombre());
        verify(promocionRepository).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retornaVacio() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<PromocionResponseDTO> resultado = marketingService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(promocionRepository).findById(99L);
    }

    @Test
    void cambiarEstado_cuandoExiste_cambiaYRetorna() {
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        Promocion promocionActualizada = new Promocion();
        promocionActualizada.setId(1L);
        promocionActualizada.setNombre("Black Friday");
        promocionActualizada.setEstado(Promocion.Estado.INACTIVO);
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocionActualizada);

        PromocionResponseDTO resultado = marketingService.cambiarEstado(1L, Promocion.Estado.INACTIVO);

        assertEquals("INACTIVO", resultado.getEstado());
        verify(promocionRepository).findById(1L);
        verify(promocionRepository).save(any(Promocion.class));
    }

    @Test
    void cambiarEstado_cuandoNoExiste_lanzaExcepcion() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> marketingService.cambiarEstado(99L, Promocion.Estado.INACTIVO));
        verify(promocionRepository).findById(99L);
        verify(promocionRepository, never()).save(any());
    }

    @Test
    void eliminar_cuandoExiste_retornaTrue() {
        when(promocionRepository.existsById(1L)).thenReturn(true);

        boolean resultado = marketingService.eliminar(1L);

        assertTrue(resultado);
        verify(promocionRepository).existsById(1L);
        verify(promocionRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_retornaFalse() {
        when(promocionRepository.existsById(99L)).thenReturn(false);

        boolean resultado = marketingService.eliminar(99L);

        assertFalse(resultado);
        verify(promocionRepository).existsById(99L);
        verify(promocionRepository, never()).deleteById(any());
    }

}
