package DJ.TIENDA.ms_catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import DJ.TIENDA.ms_catalogo.dto.ProductoDetalleDTO;
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

    @Test
    void listar_returnsAllProducts() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setPrecio(19.99);

        when(catalogoService.obtenerTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/catalogo/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto Test"));
    }

    @Test
    void verDetallesConStock_whenExists_returnsDetail() throws Exception {
        ProductoDetalleDTO detalle = new ProductoDetalleDTO();
        detalle.setId(1L);
        detalle.setNombre("Producto Test");
        detalle.setPrecio(19.99);
        detalle.setStockDisponible(10);

        when(catalogoService.obtenerDetalleConStock(1L)).thenReturn(Optional.of(detalle));

        mockMvc.perform(get("/api/catalogo/productos/1/detalles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockDisponible").value(10));
    }

    @Test
    void verDetallesConStock_whenNotExists_returns404() throws Exception {
        when(catalogoService.obtenerDetalleConStock(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/catalogo/productos/99/detalles"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_whenValid_returns201() throws Exception {
        Producto producto = new Producto();
        producto.setNombre("Nuevo Producto");
        producto.setPrecio(29.99);

        Producto creado = new Producto();
        creado.setId(1L);
        creado.setNombre("Nuevo Producto");
        creado.setPrecio(29.99);

        when(catalogoService.crear(any(Producto.class))).thenReturn(creado);

        mockMvc.perform(post("/api/catalogo/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Nuevo Producto"));
    }

    @Test
    void eliminar_whenExists_returns200() throws Exception {
        when(catalogoService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/catalogo/productos/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(catalogoService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/catalogo/productos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerPorId_whenExists_returnsProduct() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Producto Test");
        producto.setPrecio(19.99);

        when(catalogoService.obtenerTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/catalogo/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    @Test
    void obtenerPorId_whenNotExists_returns404() throws Exception {
        when(catalogoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/catalogo/productos/99"))
                .andExpect(status().isNotFound());
    }
}
