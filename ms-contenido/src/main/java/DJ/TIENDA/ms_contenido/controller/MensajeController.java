package DJ.TIENDA.ms_contenido.controller;

import DJ.TIENDA.ms_contenido.model.Mensaje;
import DJ.TIENDA.ms_contenido.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Mensajes de contacto", description = "Mensajes enviados desde el formulario de Contacto")
@RestController
@RequestMapping("/api/v1/contenido/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @Operation(summary = "Listar mensajes", description = "Retorna todos los mensajes, los mas recientes primero (panel admin)")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes obtenida")
    @GetMapping
    public ResponseEntity<List<Mensaje>> listar() {
        return ResponseEntity.ok(mensajeService.obtenerTodos());
    }

    @Operation(summary = "Enviar mensaje", description = "Guarda un mensaje del formulario de Contacto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje guardado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping
    public ResponseEntity<Mensaje> crear(@Valid @RequestBody Mensaje mensaje) {
        mensaje.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(mensajeService.crear(mensaje));
    }

    @Operation(summary = "Eliminar mensaje", description = "Elimina un mensaje de contacto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje eliminado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (mensajeService.eliminar(id)) {
            return ResponseEntity.ok("Mensaje eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Mensaje no encontrado con ID: " + id);
    }
}
