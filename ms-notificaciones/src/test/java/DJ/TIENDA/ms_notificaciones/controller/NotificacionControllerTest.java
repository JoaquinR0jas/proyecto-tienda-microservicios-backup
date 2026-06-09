package DJ.TIENDA.ms_notificaciones.controller;

import DJ.TIENDA.ms_notificaciones.dto.NotificacionResponseDTO;
import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import DJ.TIENDA.ms_notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificacionService notificacionService;

    @Test
    void enviarNotificacion_cuandoValida_retorna201() throws Exception {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setNotificacionId(1L);
        dto.setUsuarioId(10L);
        dto.setTipo("PEDIDO_CREADO");
        dto.setMensaje("Tu pedido fue creado.");
        dto.setLeida(false);
        dto.setFechaCreacion(LocalDateTime.now());

        when(notificacionService.enviarNotificacion(anyLong(), any(Notificacion.Tipo.class), any(String.class)))
                .thenReturn(dto);

        String body = """
                {"usuarioId": "10", "tipo": "PEDIDO_CREADO", "mensaje": "Tu pedido fue creado."}
                """;

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("PEDIDO_CREADO"))
                .andExpect(jsonPath("$.mensaje").value("Tu pedido fue creado."));
    }

    @Test
    void enviarNotificacion_cuandoTipoInvalido_retorna400() throws Exception {
        String body = """
                {"usuarioId": "10", "tipo": "TIPO_INVALIDO", "mensaje": "test"}
                """;

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enviarNotificacion_cuandoUsuarioIdInvalido_retorna400() throws Exception {
        String body = """
                {"usuarioId": "no-numero", "tipo": "PEDIDO_CREADO", "mensaje": "test"}
                """;

        mockMvc.perform(post("/api/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorUsuario_retorna200() throws Exception {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setNotificacionId(1L);
        dto.setUsuarioId(10L);
        dto.setTipo("PEDIDO_CREADO");

        when(notificacionService.obtenerPorUsuario(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notificaciones/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("PEDIDO_CREADO"));
    }

    @Test
    void obtenerNoLeidas_retorna200() throws Exception {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setNotificacionId(1L);
        dto.setUsuarioId(10L);
        dto.setLeida(false);

        when(notificacionService.obtenerNoLeidas(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/notificaciones/usuario/10/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leida").value(false));
    }

    @Test
    void marcarComoLeida_cuandoExiste_retorna200() throws Exception {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setNotificacionId(1L);
        dto.setLeida(true);

        when(notificacionService.marcarComoLeida(1L)).thenReturn(dto);

        mockMvc.perform(patch("/api/notificaciones/1/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));
    }

    @Test
    void marcarComoLeida_cuandoNoExiste_retorna404() throws Exception {
        when(notificacionService.marcarComoLeida(99L))
                .thenThrow(new IllegalArgumentException("Notificacion no encontrada con ID: 99"));

        mockMvc.perform(patch("/api/notificaciones/99/leer"))
                .andExpect(status().isNotFound());
    }

}
