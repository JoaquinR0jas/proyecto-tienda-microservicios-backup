package DJ.TIENDA.ms_inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.service.InventarioService;
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

@WebMvcTest(InventarioController.class)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    private static final ObjectMapper om = new ObjectMapper();

    @Test
    void listarTodoElStock_returnsAllStock() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(10L);
        inventario.setCantidad(50);

        when(inventarioService.obtenerTodo()).thenReturn(List.of(inventario));

        mockMvc.perform(get("/api/inventario/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(10));
    }

    @Test
    void verStockPorProducto_whenExists_returnsStock() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(10L);
        inventario.setCantidad(30);

        when(inventarioService.obtenerPorProductoId(10L)).thenReturn(Optional.of(inventario));

        mockMvc.perform(get("/api/inventario/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(30));
    }

    @Test
    void verStockPorProducto_whenNotExists_returns404() throws Exception {
        when(inventarioService.obtenerPorProductoId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/inventario/producto/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void guardarStock_returns201() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setProductoId(10L);
        inventario.setCantidad(100);

        Inventario guardado = new Inventario();
        guardado.setId(1L);
        guardado.setProductoId(10L);
        guardado.setCantidad(100);

        when(inventarioService.guardar(any(Inventario.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/inventario/guardar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(inventario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cantidad").value(100));
    }

    @Test
    void eliminar_whenExists_returns200() throws Exception {
        when(inventarioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(inventarioService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/inventario/99"))
                .andExpect(status().isNotFound());
    }
}
