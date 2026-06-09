package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void crearPedido_DebeRetornar201() throws Exception {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setPedidoId(100L);
        dto.setUsuarioId(1L);

        when(pedidoService.crearPedido(1L, 100L)).thenReturn(dto);

        mockMvc.perform(post("/api/pedidos/crear")
                        .param("usuarioId", "1")
                        .param("carritoId", "100"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").value(100L));
    }

    @Test
    void crearPedido_DebeRetornar400_CuandoExcepcion() throws Exception {
        when(pedidoService.crearPedido(anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("El carrito debe estar CONFIRMADO"));

        mockMvc.perform(post("/api/pedidos/crear")
                        .param("usuarioId", "1")
                        .param("carritoId", "100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPedidosPorUsuario_DebeRetornar200() throws Exception {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setPedidoId(100L);

        when(pedidoService.obtenerPedidosPorUsuario(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pedidos/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(100L));
    }

    @Test
    void obtenerPorId_DebeRetornar200_CuandoExiste() throws Exception {
        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setPedidoId(100L);

        when(pedidoService.obtenerPorId(100L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/pedidos/{pedidoId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(100L));
    }

    @Test
    void obtenerPorId_DebeRetornar404_CuandoNoExiste() throws Exception {
        when(pedidoService.obtenerPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pedidos/{pedidoId}", 999L))
                .andExpect(status().isNotFound());
    }
}
