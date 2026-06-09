package DJ.TIENDA.ms_marketing.controller;

import DJ.TIENDA.ms_marketing.dto.PromocionResponseDTO;
import DJ.TIENDA.ms_marketing.model.Promocion;
import DJ.TIENDA.ms_marketing.service.MarketingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MarketingController.class)
class MarketingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MarketingService marketingService;

    @Test
    void crearPromocion_retorna201() throws Exception {
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setPromocionId(1L);
        dto.setNombre("Black Friday");
        dto.setDescripcion("Descuento especial");
        dto.setDescuentoPorcentaje(50);
        dto.setEstado("ACTIVO");
        dto.setFechaCreacion(LocalDateTime.now());

        when(marketingService.crearPromocion(any(Promocion.class))).thenReturn(dto);

        String body = "{\"nombre\":\"Black Friday\",\"descripcion\":\"Descuento especial\",\"descuentoPorcentaje\":50}";

        mockMvc.perform(post("/api/marketing/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Black Friday"))
                .andExpect(jsonPath("$.descuentoPorcentaje").value(50));
    }

    @Test
    void obtenerTodas_retorna200() throws Exception {
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setPromocionId(1L);
        dto.setNombre("Black Friday");

        when(marketingService.obtenerTodas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/marketing/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Black Friday"));
    }

    @Test
    void obtenerTodas_cuandoVacio_retorna200() throws Exception {
        when(marketingService.obtenerTodas()).thenReturn(List.of());

        mockMvc.perform(get("/api/marketing/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void obtenerActivas_retorna200() throws Exception {
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setPromocionId(1L);
        dto.setNombre("Black Friday");

        when(marketingService.obtenerActivas()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/marketing/promociones/activas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Black Friday"));
    }

    @Test
    void obtenerPorId_cuandoExiste_retorna200() throws Exception {
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setPromocionId(1L);
        dto.setNombre("Black Friday");

        when(marketingService.obtenerPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/marketing/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Black Friday"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_retorna404() throws Exception {
        when(marketingService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/marketing/promociones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cambiarEstado_cuandoValido_retorna200() throws Exception {
        PromocionResponseDTO dto = new PromocionResponseDTO();
        dto.setPromocionId(1L);
        dto.setNombre("Black Friday");
        dto.setEstado("INACTIVO");

        when(marketingService.cambiarEstado(eq(1L), eq(Promocion.Estado.INACTIVO))).thenReturn(dto);

        String body = "{\"estado\":\"INACTIVO\"}";

        mockMvc.perform(patch("/api/marketing/promociones/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVO"));
    }

    @Test
    void cambiarEstado_cuandoEstadoInvalido_retorna400() throws Exception {
        String body = "{\"estado\":\"INEXISTENTE\"}";

        mockMvc.perform(patch("/api/marketing/promociones/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_cuandoExiste_retorna200() throws Exception {
        when(marketingService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/marketing/promociones/1"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_cuandoNoExiste_retorna404() throws Exception {
        when(marketingService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/marketing/promociones/99"))
                .andExpect(status().isNotFound());
    }

}
