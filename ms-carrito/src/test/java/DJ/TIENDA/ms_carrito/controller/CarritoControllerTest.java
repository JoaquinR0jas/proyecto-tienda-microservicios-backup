package DJ.TIENDA.ms_carrito.controller;

import DJ.TIENDA.ms_carrito.dto.CarritoResponseDTO;
import DJ.TIENDA.ms_carrito.model.Carrito;
import DJ.TIENDA.ms_carrito.service.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService carritoService;

    @Test
    void verCarrito_DebeRetornar200() throws Exception {
        Long usuarioId = 1L;
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setCarritoId(100L);
        dto.setUsuarioId(usuarioId);

        when(carritoService.verCarrito(usuarioId)).thenReturn(dto);

        mockMvc.perform(get("/api/carrito/{usuarioId}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carritoId").value(100L))
                .andExpect(jsonPath("$.usuarioId").value(usuarioId));
    }

    @Test
    void verCarritoPorId_DebeRetornar200_CuandoExiste() throws Exception {
        Long carritoId = 100L;
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setCarritoId(carritoId);

        when(carritoService.verCarritoPorId(carritoId)).thenReturn(dto);

        mockMvc.perform(get("/api/carrito/detalle/{carritoId}", carritoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carritoId").value(carritoId));
    }

    @Test
    void verCarritoPorId_DebeRetornar404_CuandoNoExiste() throws Exception {
        when(carritoService.verCarritoPorId(999L)).thenThrow(new IllegalArgumentException("Carrito no encontrado"));

        mockMvc.perform(get("/api/carrito/detalle/{carritoId}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarProducto_DebeRetornar200() throws Exception {
        Long usuarioId = 1L;
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setCarritoId(100L);

        when(carritoService.agregarProducto(eq(usuarioId), eq(10L), eq(2))).thenReturn(dto);

        mockMvc.perform(post("/api/carrito/{usuarioId}/agregar", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("productoId", 10, "cantidad", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carritoId").value(100L));
    }

    @Test
    void agregarProducto_DebeRetornar400_CuandoExcepcion() throws Exception {
        when(carritoService.agregarProducto(anyLong(), anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("Stock insuficiente"));

        mockMvc.perform(post("/api/carrito/{usuarioId}/agregar", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("productoId", 10, "cantidad", 99))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarItem_DebeRetornar200() throws Exception {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setCarritoId(100L);

        when(carritoService.eliminarItem(100L, 1L)).thenReturn(dto);

        mockMvc.perform(delete("/api/carrito/{carritoId}/item/{itemId}", 100L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carritoId").value(100L));
    }

    @Test
    void vaciarCarrito_DebeRetornar200() throws Exception {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setCarritoId(100L);

        when(carritoService.vaciarCarrito(1L)).thenReturn(dto);

        mockMvc.perform(delete("/api/carrito/{usuarioId}/vaciar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carritoId").value(100L));
    }

    @Test
    void cambiarEstado_DebeRetornar200() throws Exception {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setEstado("CONFIRMADO");

        when(carritoService.cambiarEstado(eq(100L), eq(Carrito.Estado.CONFIRMADO))).thenReturn(dto);

        mockMvc.perform(patch("/api/carrito/{carritoId}/estado", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("estado", "CONFIRMADO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void cambiarEstado_DebeRetornar400_CuandoEstadoInvalido() throws Exception {
        mockMvc.perform(patch("/api/carrito/{carritoId}/estado", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(Map.of("estado", "INVALIDO"))))
                .andExpect(status().isBadRequest());
    }
}
