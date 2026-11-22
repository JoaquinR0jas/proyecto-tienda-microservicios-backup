package DJ.TIENDA.ms_contenido.controller;

import DJ.TIENDA.ms_contenido.model.Articulo;
import DJ.TIENDA.ms_contenido.service.ArticuloService;
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

@WebMvcTest(ArticuloController.class)
class ArticuloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticuloService articuloService;

    @Test
    void listar_byDefault_returnsPublishedOnly() throws Exception {
        Articulo articulo = new Articulo();
        articulo.setSlug("detalle-1");

        when(articuloService.obtenerArticulos(false)).thenReturn(List.of(articulo));

        mockMvc.perform(get("/api/v1/contenido/articulos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("detalle-1"));
    }

    @Test
    void obtenerPorSlug_whenNotExists_returns404() throws Exception {
        when(articuloService.obtenerPorSlug("no-existe")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/contenido/articulos/no-existe"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_whenSlugAlreadyExists_returns409() throws Exception {
        when(articuloService.crear(any(Articulo.class)))
                .thenThrow(new IllegalArgumentException("Ya existe un articulo con el slug: detalle-1"));

        mockMvc.perform(post("/api/v1/contenido/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"detalle-1\",\"titulo\":\"T\",\"resumen\":\"R\",\"contenido\":\"C\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void crear_whenMissingTitle_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/contenido/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\":\"nuevo\",\"resumen\":\"R\",\"contenido\":\"C\"}"))
                .andExpect(status().isBadRequest());
    }
}
