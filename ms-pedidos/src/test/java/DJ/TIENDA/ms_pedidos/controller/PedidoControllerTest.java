package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.CrearPedidoDTO;
import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
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

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    private static final String PEDIDO_VALIDO = """
            {
              "usuarioId": 3,
              "cliente": {"nombre": "Camila", "apellidos": "Soto Perez", "email": "camila@gmail.com"},
              "entrega": {"tipo": "RETIRO"},
              "medioPago": "DEBITO",
              "items": [{"productoId": 1, "cantidad": 2}]
            }
            """;

    @Test
    void crearPedido_whenValid_returns201() throws Exception {
        PedidoResponseDTO respuesta = new PedidoResponseDTO();
        respuesta.setPedidoId(1L);
        respuesta.setEstado("PAGADO");

        when(pedidoService.crearPedido(any(CrearPedidoDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    void crearPedido_whenCartIsEmpty_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO.replace("[{\"productoId\": 1, \"cantidad\": 2}]", "[]")))
                .andExpect(status().isBadRequest());

        verify(pedidoService, never()).crearPedido(any());
    }

    @Test
    void crearPedido_whenNoUser_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO.replace("\"usuarioId\": 3,", "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearPedido_whenServiceRejectsData_returns400() throws Exception {
        when(pedidoService.crearPedido(any(CrearPedidoDTO.class)))
                .thenThrow(new IllegalArgumentException("Producto no encontrado con ID: 1"));

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PEDIDO_VALIDO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodos_returnsAllOrders() throws Exception {
        PedidoResponseDTO pedido = new PedidoResponseDTO();
        pedido.setPedidoId(1L);

        when(pedidoService.obtenerTodos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pedidoId").value(1));
    }

    @Test
    void obtenerPorId_whenNotExists_returns404() throws Exception {
        when(pedidoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/pedidos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorId_whenVendor_returns200() throws Exception {
        PedidoResponseDTO pedido = new PedidoResponseDTO();
        pedido.setPedidoId(1L);
        pedido.setUsuarioId(7L);

        when(pedidoService.obtenerPorId(1L)).thenReturn(Optional.of(pedido));

        // El vendedor puede abrir el detalle de cualquier pedido
        mockMvc.perform(get("/api/v1/pedidos/1")
                        .header("X-Usuario-Id", 9L)
                        .header("X-Usuario-Rol", "VENDEDOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(1));
    }

    @Test
    void obtenerPorId_whenOtherClient_returns403() throws Exception {
        PedidoResponseDTO pedido = new PedidoResponseDTO();
        pedido.setPedidoId(1L);
        pedido.setUsuarioId(7L);

        when(pedidoService.obtenerPorId(1L)).thenReturn(Optional.of(pedido));

        // Un cliente no puede ver el pedido de otra persona
        mockMvc.perform(get("/api/v1/pedidos/1")
                        .header("X-Usuario-Id", 8L)
                        .header("X-Usuario-Rol", "CLIENTE"))
                .andExpect(status().isForbidden());
    }
}
