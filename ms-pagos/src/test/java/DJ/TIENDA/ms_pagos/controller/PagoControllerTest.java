package DJ.TIENDA.ms_pagos.controller;

import DJ.TIENDA.ms_pagos.dto.PagoRequestDTO;
import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Boleta;
import DJ.TIENDA.ms_pagos.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @Test
    void procesarPago_whenApproved_returns201WithBoleta() throws Exception {
        Boleta boleta = new Boleta();
        boleta.setFolio(1L);
        PagoResponseDTO respuesta = new PagoResponseDTO();
        respuesta.setPagoId(1L);
        respuesta.setEstado("APROBADO");
        respuesta.setBoleta(boleta);

        when(pagoService.procesarPago(any(PagoRequestDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\":1,\"usuarioId\":10,\"monto\":11900,\"medio\":\"DEBITO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.boleta.folio").value(1));
    }

    @Test
    void procesarPago_whenMissingMonto_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\":1,\"usuarioId\":10}"))
                .andExpect(status().isBadRequest());

        verify(pagoService, never()).procesarPago(any());
    }

    @Test
    void procesarPago_whenInvalidMedio_returns400() throws Exception {
        when(pagoService.procesarPago(any(PagoRequestDTO.class)))
                .thenThrow(new IllegalArgumentException("Medio de pago invalido: bitcoin"));

        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\":1,\"usuarioId\":10,\"monto\":100,\"medio\":\"bitcoin\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPagosPorUsuario_returnsList() throws Exception {
        PagoResponseDTO pago = new PagoResponseDTO();
        pago.setPagoId(1L);
        pago.setUsuarioId(10L);

        when(pagoService.obtenerPagosPorUsuario(10L)).thenReturn(List.of(pago));

        mockMvc.perform(get("/api/v1/pagos/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(10));
    }

    @Test
    void obtenerBoleta_whenExists_returns200() throws Exception {
        Boleta boleta = new Boleta();
        boleta.setFolio(7L);

        when(pagoService.obtenerBoletaPorFolio(7L)).thenReturn(Optional.of(boleta));

        mockMvc.perform(get("/api/v1/pagos/boletas/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(7));
    }

    @Test
    void obtenerBoleta_whenNotExists_returns404() throws Exception {
        when(pagoService.obtenerBoletaPorFolio(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pagos/boletas/99"))
                .andExpect(status().isNotFound());
    }
}
