package DJ.TIENDA.ms_pagos.controller;

import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @Test
    void procesarPago_DebeRetornar201() throws Exception {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setPagoId(100L);
        dto.setEstado("COMPLETADO");

        when(pagoService.procesarPago(eq(1L), eq(10L), eq(199.99))).thenReturn(dto);

        mockMvc.perform(post("/api/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("pedidoId", 1, "usuarioId", 10, "monto", 199.99))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pagoId").value(100L))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    void procesarPago_DebeRetornar400_CuandoExcepcion() throws Exception {
        when(pagoService.procesarPago(anyLong(), anyLong(), anyDouble()))
                .thenThrow(new IllegalArgumentException("Error de pago"));

        mockMvc.perform(post("/api/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("pedidoId", 1, "usuarioId", 10, "monto", 100.0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPagosPorPedido_DebeRetornar200() throws Exception {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setPagoId(100L);

        when(pagoService.obtenerPagosPorPedido(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pagos/pedido/{pedidoId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pagoId").value(100L));
    }

    @Test
    void obtenerPagosPorUsuario_DebeRetornar200() throws Exception {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setPagoId(100L);

        when(pagoService.obtenerPagosPorUsuario(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pagos/usuario/{usuarioId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pagoId").value(100L));
    }
}
