package DJ.TIENDA.ms_contenido.controller;

import DJ.TIENDA.ms_contenido.model.Mensaje;
import DJ.TIENDA.ms_contenido.service.MensajeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MensajeController.class)
class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensajeService mensajeService;

    @Test
    void crear_whenValid_returns201() throws Exception {
        Mensaje guardado = new Mensaje();
        guardado.setId(1L);
        guardado.setNombre("Camila");

        when(mensajeService.crear(any(Mensaje.class))).thenReturn(guardado);

        mockMvc.perform(post("/api/v1/contenido/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Camila\",\"email\":\"camila@gmail.com\",\"comentario\":\"Hola\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // caso borde: un nombre con solo espacios no es valido, igual que en el formulario del frontend
    @Test
    void crear_whenNameIsOnlySpaces_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/contenido/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"     \",\"email\":\"camila@gmail.com\",\"comentario\":\"Hola\"}"))
                .andExpect(status().isBadRequest());

        verify(mensajeService, never()).crear(any());
    }

    @Test
    void crear_whenEmailIsInvalid_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/contenido/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Camila\",\"email\":\"no-es-correo\",\"comentario\":\"Hola\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_returnsMessages() throws Exception {
        Mensaje mensaje = new Mensaje();
        mensaje.setId(1L);
        mensaje.setNombre("Camila");

        when(mensajeService.obtenerTodos()).thenReturn(List.of(mensaje));

        mockMvc.perform(get("/api/v1/contenido/mensajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Camila"));
    }

    @Test
    void eliminar_whenNotExists_returns404() throws Exception {
        when(mensajeService.eliminar(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/contenido/mensajes/99"))
                .andExpect(status().isNotFound());
    }
}
