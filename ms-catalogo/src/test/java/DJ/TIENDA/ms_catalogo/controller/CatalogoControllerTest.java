package DJ.TIENDA.ms_catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.service.CatalogoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogoController.class)
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogoService catalogoService;

    private static final ObjectMapper om = new ObjectMapper();

    private Producto productoValido() {
        Producto producto = new Producto();
        producto.setCodigo("FF-001");
        producto.setNombre("Producto Test");
        producto.setPrecio(19990.0);
        producto.setStock(10);
        producto.setCategoriaId(1L);
        return producto;
    }

    @Test
    void listar_returnsAllProducts() throws Exception {
        Producto producto = productoValido();
        producto.setId(1L);

        when(catalogoService.obtenerTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/catalogo/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto Test"));
    }

    @Test
    void listar_soloActivos_usesActiveList() throws Exception {
        when(catalogoService.obtenerActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/catalogo/productos").param("soloActivos", "true"))
                .andExpect(status().isOk());

        verify(catalogoService).obtenerActivos();
        verify(catalogoService, never()).obtenerTodos();
    }

    @Test
    void listarOfertas_returnsPrecioFinal() throws Exception {
        Producto producto = productoValido();
        producto.setDescuento(20);

        when(catalogoService.obtenerOfertas()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/v1/catalogo/productos/ofertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].precioFinal").value(15992.0));
    }

    @Test
    void obtenerPorId_whenExists_returnsProduct() throws Exception {
        Producto producto = productoValido();
        producto.setId(1L);

        when(catalogoService.obtenerPorId(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/api/v1/catalogo/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("FF-001"));
    }

    @Test
    void obtenerPorId_whenNotExists_returns404() throws Exception {
        when(catalogoService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/catalogo/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_whenValid_returns201() throws Exception {
        Producto creado = productoValido();
        creado.setId(1L);

        when(catalogoService.crear(any(Producto.class))).thenReturn(creado);

        mockMvc.perform(post("/api/v1/catalogo/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(productoValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_whenCodeAlreadyExists_returns409() throws Exception {
        when(catalogoService.crear(any(Producto.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un producto con el codigo: FF-001"));

        mockMvc.perform(post("/api/v1/catalogo/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(productoValido())))
                .andExpect(status().isConflict());
    }

    @Test
    void crear_whenNegativeStock_returns400() throws Exception {
        Producto producto = productoValido();
        producto.setStock(-1);

        mockMvc.perform(post("/api/v1/catalogo/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(producto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_whenNotExists_returns404() throws Exception {
        when(catalogoService.actualizar(eq(99L), any(Producto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/catalogo/productos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(productoValido())))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_whenExists_returns200() throws Exception {
        when(catalogoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/catalogo/productos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(catalogoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/catalogo/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void descontarStock_whenNotEnoughStock_returns400() throws Exception {
        doThrow(new IllegalArgumentException("No hay stock suficiente de Producto Test"))
                .when(catalogoService).descontarStock(anyList());

        mockMvc.perform(post("/api/v1/catalogo/productos/descontar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"productoId\":1,\"cantidad\":99}]"))
                .andExpect(status().isBadRequest());
    }
}
