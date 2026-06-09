package DJ.TIENDA.ms_envios.controller;

import DJ.TIENDA.ms_envios.dto.EnvioResponseDTO;
import DJ.TIENDA.ms_envios.model.Envio;
import DJ.TIENDA.ms_envios.service.EnvioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnvioController.class)
class EnvioControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    @Test
    void crearEnvio_DebeRetornar201() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setEnvioId(100L);

        when(envioService.crearEnvio(1L, 10L)).thenReturn(dto);

        mockMvc.perform(post("/api/envios/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("pedidoId", 1L, "usuarioId", 10L))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.envioId").value(100L));
    }

    @Test
    void crearEnvio_DebeRetornar400_CuandoExcepcion() throws Exception {
        when(envioService.crearEnvio(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Ya existe un envio"));

        mockMvc.perform(post("/api/envios/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("pedidoId", 1L, "usuarioId", 10L))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_DebeRetornar200() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setEstado("EN_CAMINO");

        when(envioService.actualizarEstado(eq(100L), eq(Envio.Estado.EN_CAMINO))).thenReturn(dto);

        mockMvc.perform(patch("/api/envios/{envioId}/estado", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("estado", "EN_CAMINO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_CAMINO"));
    }

    @Test
    void actualizarEstado_DebeRetornar400_CuandoEstadoInvalido() throws Exception {
        mockMvc.perform(patch("/api/envios/{envioId}/estado", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("estado", "INVALIDO"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorPedido_DebeRetornar200_CuandoExiste() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setEnvioId(100L);

        when(envioService.obtenerPorPedido(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/envios/pedido/{pedidoId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.envioId").value(100L));
    }

    @Test
    void obtenerPorPedido_DebeRetornar404_CuandoNoExiste() throws Exception {
        when(envioService.obtenerPorPedido(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/envios/pedido/{pedidoId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorUsuario_DebeRetornar200() throws Exception {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setEnvioId(100L);

        when(envioService.obtenerPorUsuario(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/envios/usuario/{usuarioId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].envioId").value(100L));
    }

    @Test
    void obtenerPorUsuario_DebeRetornarListaVacia() throws Exception {
        when(envioService.obtenerPorUsuario(999L)).thenReturn(List.of());

        mockMvc.perform(get("/api/envios/usuario/{usuarioId}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
