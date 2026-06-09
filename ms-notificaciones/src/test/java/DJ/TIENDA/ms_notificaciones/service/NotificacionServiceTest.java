package DJ.TIENDA.ms_notificaciones.service;

import DJ.TIENDA.ms_notificaciones.dto.NotificacionResponseDTO;
import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import DJ.TIENDA.ms_notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Captor
    private ArgumentCaptor<Notificacion> notificacionCaptor;

    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion();
        notificacion.setId(1L);
        notificacion.setUsuarioId(10L);
        notificacion.setTipo(Notificacion.Tipo.PEDIDO_CREADO);
        notificacion.setMensaje("Tu pedido fue creado.");
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void enviarNotificacion_guardaYRetornaRespuesta() {
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> {
            Notificacion n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        NotificacionResponseDTO resultado = notificacionService.enviarNotificacion(10L, Notificacion.Tipo.PEDIDO_CREADO, "Tu pedido fue creado.");

        assertNotNull(resultado);
        assertEquals(1L, resultado.getNotificacionId());
        assertEquals(10L, resultado.getUsuarioId());
        assertEquals("PEDIDO_CREADO", resultado.getTipo());
        assertEquals("Tu pedido fue creado.", resultado.getMensaje());
        assertFalse(resultado.getLeida());

        verify(notificacionRepository).save(notificacionCaptor.capture());
        Notificacion capturada = notificacionCaptor.getValue();
        assertEquals(10L, capturada.getUsuarioId());
        assertEquals(Notificacion.Tipo.PEDIDO_CREADO, capturada.getTipo());
        assertEquals("Tu pedido fue creado.", capturada.getMensaje());
    }

    @Test
    void obtenerPorUsuario_retornaLista() {
        when(notificacionRepository.findByUsuarioId(10L)).thenReturn(List.of(notificacion));

        List<NotificacionResponseDTO> resultado = notificacionService.obtenerPorUsuario(10L);

        assertEquals(1, resultado.size());
        assertEquals("PEDIDO_CREADO", resultado.get(0).getTipo());
        verify(notificacionRepository).findByUsuarioId(10L);
    }

    @Test
    void obtenerPorUsuario_cuandoVacio_retornaListaVacia() {
        when(notificacionRepository.findByUsuarioId(10L)).thenReturn(List.of());

        List<NotificacionResponseDTO> resultado = notificacionService.obtenerPorUsuario(10L);

        assertTrue(resultado.isEmpty());
        verify(notificacionRepository).findByUsuarioId(10L);
    }

    @Test
    void obtenerNoLeidas_retornaSoloNoLeidas() {
        Notificacion leida = new Notificacion();
        leida.setId(2L);
        leida.setUsuarioId(10L);
        leida.setLeida(true);
        leida.setTipo(Notificacion.Tipo.PAGO_COMPLETADO);

        when(notificacionRepository.findByUsuarioIdAndLeida(10L, false)).thenReturn(List.of(notificacion));

        List<NotificacionResponseDTO> resultado = notificacionService.obtenerNoLeidas(10L);

        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getLeida());
        verify(notificacionRepository).findByUsuarioIdAndLeida(10L, false);
    }

    @Test
    void marcarComoLeida_cuandoExiste_marcaYRetorna() {
        Notificacion notificacionLeida = new Notificacion();
        notificacionLeida.setId(1L);
        notificacionLeida.setUsuarioId(10L);
        notificacionLeida.setTipo(Notificacion.Tipo.PEDIDO_CREADO);
        notificacionLeida.setMensaje("Tu pedido fue creado.");
        notificacionLeida.setLeida(true);

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionLeida);

        NotificacionResponseDTO resultado = notificacionService.marcarComoLeida(1L);

        assertTrue(resultado.getLeida());
        verify(notificacionRepository).findById(1L);
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void marcarComoLeida_cuandoNoExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> notificacionService.marcarComoLeida(99L));
        verify(notificacionRepository).findById(99L);
        verify(notificacionRepository, never()).save(any());
    }

}
