package DJ.TIENDA.ms_resenas.service;

import DJ.TIENDA.ms_resenas.dto.ResenaResponseDTO;
import DJ.TIENDA.ms_resenas.model.Resena;
import DJ.TIENDA.ms_resenas.repository.ResenaRepository;
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
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resena;

    @BeforeEach
    void setUp() {
        resena = new Resena();
        resena.setId(1L);
        resena.setProductoId(100L);
        resena.setUsuarioId(10L);
        resena.setPuntuacion(4);
        resena.setComentario("Muy buen producto.");
        resena.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void crearResena_guardaYRetornaRespuesta() {
        when(resenaRepository.save(resena)).thenReturn(resena);

        ResenaResponseDTO resultado = resenaService.crearResena(resena);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getResenaId());
        assertEquals(100L, resultado.getProductoId());
        assertEquals(10L, resultado.getUsuarioId());
        assertEquals(4, resultado.getPuntuacion());
        assertEquals("Muy buen producto.", resultado.getComentario());
        verify(resenaRepository).save(resena);
    }

    @Test
    void obtenerPorProducto_retornaLista() {
        Resena resena2 = new Resena();
        resena2.setId(2L);
        resena2.setProductoId(100L);
        resena2.setPuntuacion(5);
        resena2.setComentario("Excelente.");

        when(resenaRepository.findByProductoId(100L)).thenReturn(List.of(resena, resena2));

        List<ResenaResponseDTO> resultado = resenaService.obtenerPorProducto(100L);

        assertEquals(2, resultado.size());
        assertEquals(4, resultado.get(0).getPuntuacion());
        assertEquals(5, resultado.get(1).getPuntuacion());
        verify(resenaRepository).findByProductoId(100L);
    }

    @Test
    void obtenerPorProducto_cuandoVacio_retornaListaVacia() {
        when(resenaRepository.findByProductoId(100L)).thenReturn(List.of());

        List<ResenaResponseDTO> resultado = resenaService.obtenerPorProducto(100L);

        assertTrue(resultado.isEmpty());
        verify(resenaRepository).findByProductoId(100L);
    }

    @Test
    void obtenerPorUsuario_retornaLista() {
        when(resenaRepository.findByUsuarioId(10L)).thenReturn(List.of(resena));

        List<ResenaResponseDTO> resultado = resenaService.obtenerPorUsuario(10L);

        assertEquals(1, resultado.size());
        assertEquals(4, resultado.get(0).getPuntuacion());
        verify(resenaRepository).findByUsuarioId(10L);
    }

    @Test
    void obtenerPromedio_cuandoExistenResenas_retornaPromedioRedondeado() {
        when(resenaRepository.promedioByProductoId(100L)).thenReturn(4.2667);

        Double resultado = resenaService.obtenerPromedio(100L);

        assertEquals(4.3, resultado, 0.01);
        verify(resenaRepository).promedioByProductoId(100L);
    }

    @Test
    void obtenerPromedio_cuandoNoExistenResenas_retornaCero() {
        when(resenaRepository.promedioByProductoId(100L)).thenReturn(null);

        Double resultado = resenaService.obtenerPromedio(100L);

        assertEquals(0.0, resultado);
        verify(resenaRepository).promedioByProductoId(100L);
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaResena() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        Optional<ResenaResponseDTO> resultado = resenaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(4, resultado.get().getPuntuacion());
        verify(resenaRepository).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retornaVacio() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ResenaResponseDTO> resultado = resenaService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
        verify(resenaRepository).findById(99L);
    }

    @Test
    void eliminar_cuandoExiste_retornaTrue() {
        when(resenaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = resenaService.eliminar(1L);

        assertTrue(resultado);
        verify(resenaRepository).existsById(1L);
        verify(resenaRepository).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_retornaFalse() {
        when(resenaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = resenaService.eliminar(99L);

        assertFalse(resultado);
        verify(resenaRepository).existsById(99L);
        verify(resenaRepository, never()).deleteById(any());
    }

}
