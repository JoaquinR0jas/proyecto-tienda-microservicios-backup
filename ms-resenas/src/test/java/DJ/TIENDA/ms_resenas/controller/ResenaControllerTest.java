package DJ.TIENDA.ms_resenas.controller;

import DJ.TIENDA.ms_resenas.dto.ResenaResponseDTO;
import DJ.TIENDA.ms_resenas.model.Resena;
import DJ.TIENDA.ms_resenas.service.ResenaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenaController.class)
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResenaService resenaService;

    @Test
    void crearResena_retorna201() throws Exception {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setResenaId(1L);
        dto.setProductoId(100L);
        dto.setUsuarioId(10L);
        dto.setPuntuacion(4);
        dto.setComentario("Muy buen producto.");

        when(resenaService.crearResena(any(Resena.class))).thenReturn(dto);

        String body = "{\"productoId\":100,\"usuarioId\":10,\"puntuacion\":4,\"comentario\":\"Muy buen producto.\"}";

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.puntuacion").value(4))
                .andExpect(jsonPath("$.comentario").value("Muy buen producto."));
    }

    @Test
    void obtenerPorProducto_retorna200() throws Exception {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setResenaId(1L);
        dto.setProductoId(100L);
        dto.setPuntuacion(4);
        dto.setComentario("Muy buen producto.");

        when(resenaService.obtenerPorProducto(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/resenas/producto/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].puntuacion").value(4));
    }

    @Test
    void obtenerPromedio_retorna200() throws Exception {
        when(resenaService.obtenerPromedio(100L)).thenReturn(4.3);

        mockMvc.perform(get("/api/resenas/producto/100/promedio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productoId").value(100))
                .andExpect(jsonPath("$.promedioPuntuacion").value(4.3))
                .andExpect(jsonPath("$.escala").value("1 a 5 estrellas"));
    }

    @Test
    void obtenerPromedio_sinResenas_retornaCero() throws Exception {
        when(resenaService.obtenerPromedio(100L)).thenReturn(0.0);

        mockMvc.perform(get("/api/resenas/producto/100/promedio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedioPuntuacion").value(0.0));
    }

    @Test
    void obtenerPorUsuario_retorna200() throws Exception {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setResenaId(1L);
        dto.setUsuarioId(10L);
        dto.setPuntuacion(4);

        when(resenaService.obtenerPorUsuario(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/resenas/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].puntuacion").value(4));
    }

    @Test
    void eliminar_cuandoExiste_retorna200() throws Exception {
        when(resenaService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/resenas/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_cuandoNoExiste_retorna404() throws Exception {
        when(resenaService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/resenas/99"))
                .andExpect(status().isNotFound());
    }

}
