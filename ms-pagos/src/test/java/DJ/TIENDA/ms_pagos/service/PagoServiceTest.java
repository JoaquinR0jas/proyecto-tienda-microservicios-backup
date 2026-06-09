package DJ.TIENDA.ms_pagos.service;

import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Pago;
import DJ.TIENDA.ms_pagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @InjectMocks
    private PagoService pagoService;

    @Mock
    private PagoRepository pagoRepository;

    @Test
    void procesarPago_DebeGuardarYRetornarRespuesta() {
        Long pedidoId = 1L;
        Long usuarioId = 10L;
        Double monto = 199.99;

        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> {
            Pago p = i.getArgument(0);
            p.setId(100L);
            return p;
        });

        PagoResponseDTO resultado = pagoService.procesarPago(pedidoId, usuarioId, monto);

        assertNotNull(resultado);
        assertEquals(pedidoId, resultado.getPedidoId());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(monto, resultado.getMonto());
        assertNotNull(resultado.getEstado());
        assertTrue(resultado.getEstado().equals("COMPLETADO") || resultado.getEstado().equals("FALLIDO"));
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void obtenerPagosPorPedido_DebeRetornarLista() {
        Long pedidoId = 1L;
        Pago pago = new Pago();
        pago.setId(100L);
        pago.setPedidoId(pedidoId);

        when(pagoRepository.findByPedidoId(pedidoId)).thenReturn(List.of(pago));

        List<PagoResponseDTO> resultados = pagoService.obtenerPagosPorPedido(pedidoId);

        assertEquals(1, resultados.size());
        assertEquals(pedidoId, resultados.get(0).getPedidoId());
    }

    @Test
    void obtenerPagosPorPedido_DebeRetornarListaVacia_CuandoNoHay() {
        when(pagoRepository.findByPedidoId(999L)).thenReturn(List.of());

        List<PagoResponseDTO> resultados = pagoService.obtenerPagosPorPedido(999L);

        assertTrue(resultados.isEmpty());
    }

    @Test
    void obtenerPagosPorUsuario_DebeRetornarLista() {
        Long usuarioId = 10L;
        Pago pago = new Pago();
        pago.setId(100L);
        pago.setUsuarioId(usuarioId);

        when(pagoRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(pago));

        List<PagoResponseDTO> resultados = pagoService.obtenerPagosPorUsuario(usuarioId);

        assertEquals(1, resultados.size());
        assertEquals(usuarioId, resultados.get(0).getUsuarioId());
    }

    @Test
    void obtenerPagosPorUsuario_DebeRetornarListaVacia_CuandoNoHay() {
        when(pagoRepository.findByUsuarioId(999L)).thenReturn(List.of());

        List<PagoResponseDTO> resultados = pagoService.obtenerPagosPorUsuario(999L);

        assertTrue(resultados.isEmpty());
    }
}
