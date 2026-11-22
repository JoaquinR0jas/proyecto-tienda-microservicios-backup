package DJ.TIENDA.ms_pagos.service;

import DJ.TIENDA.ms_pagos.dto.PagoRequestDTO;
import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Boleta;
import DJ.TIENDA.ms_pagos.model.Pago;
import DJ.TIENDA.ms_pagos.repository.BoletaRepository;
import DJ.TIENDA.ms_pagos.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private BoletaRepository boletaRepository;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequestDTO solicitud;

    @BeforeEach
    void setUp() {
        solicitud = new PagoRequestDTO();
        solicitud.setPedidoId(1L);
        solicitud.setUsuarioId(10L);
        solicitud.setMonto(11900.0);
        solicitud.setMedio("credito");
    }

    @Test
    void procesarPago_whenApproved_savesPagoAndEmitsBoletaWithIva() {
        when(boletaRepository.findTopByOrderByFolioDesc()).thenReturn(Optional.empty());
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponseDTO resultado = pagoService.procesarPago(solicitud);

        assertEquals("APROBADO", resultado.getEstado());
        assertEquals("CREDITO", resultado.getMedio());
        assertNotNull(resultado.getBoleta());
        assertEquals(1L, resultado.getBoleta().getFolio());
        assertEquals(10000.0, resultado.getBoleta().getNeto());
        assertEquals(1900.0, resultado.getBoleta().getIva());
        assertEquals(11900.0, resultado.getBoleta().getTotal());
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void procesarPago_whenApproved_usesNextFolio() {
        Boleta ultima = new Boleta();
        ultima.setFolio(41L);
        when(boletaRepository.findTopByOrderByFolioDesc()).thenReturn(Optional.of(ultima));
        when(boletaRepository.save(any(Boleta.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponseDTO resultado = pagoService.procesarPago(solicitud);

        assertEquals(42L, resultado.getBoleta().getFolio());
    }

    @Test
    void procesarPago_whenSimulatedRejection_savesRejectedPagoWithoutBoleta() {
        solicitud.setSimularRechazo(true);

        PagoResponseDTO resultado = pagoService.procesarPago(solicitud);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());
        assertEquals(Pago.Estado.RECHAZADO, captor.getValue().getEstado());
        assertEquals("RECHAZADO", resultado.getEstado());
        assertNull(resultado.getBoleta());
        verify(boletaRepository, never()).save(any());
    }

    @Test
    void procesarPago_whenMedioIsMissing_defaultsToDebito() {
        solicitud.setMedio(null);
        solicitud.setSimularRechazo(true);

        PagoResponseDTO resultado = pagoService.procesarPago(solicitud);

        assertEquals("DEBITO", resultado.getMedio());
    }

    @Test
    void procesarPago_whenMedioIsInvalid_throwsException() {
        solicitud.setMedio("bitcoin");

        assertThrows(IllegalArgumentException.class, () -> pagoService.procesarPago(solicitud));
        verify(pagoRepository, never()).save(any());
    }

    @Test
    void obtenerPagosPorPedido_includesBoletaWhenExists() {
        Pago pago = new Pago();
        pago.setId(5L);
        pago.setPedidoId(1L);
        pago.setUsuarioId(10L);
        pago.setMonto(11900.0);
        pago.setEstado(Pago.Estado.APROBADO);
        Boleta boleta = new Boleta();
        boleta.setFolio(1L);

        when(pagoRepository.findByPedidoId(1L)).thenReturn(List.of(pago));
        when(boletaRepository.findByPagoId(5L)).thenReturn(Optional.of(boleta));

        List<PagoResponseDTO> resultado = pagoService.obtenerPagosPorPedido(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getBoleta().getFolio());
    }

    @Test
    void obtenerBoletaPorFolio_whenNotExists_returnsEmpty() {
        when(boletaRepository.findByFolio(99L)).thenReturn(Optional.empty());

        assertFalse(pagoService.obtenerBoletaPorFolio(99L).isPresent());
    }
}
