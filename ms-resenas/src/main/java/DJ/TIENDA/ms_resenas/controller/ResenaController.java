package DJ.TIENDA.ms_resenas.controller;

import DJ.TIENDA.ms_resenas.dto.ResenaResponseDTO;
import DJ.TIENDA.ms_resenas.model.Resena;
import DJ.TIENDA.ms_resenas.service.ResenaService;
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
import java.util.Map;

@Tag(name = "Resenas", description = "Gestion de resenas y valoraciones de productos")
@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @Operation(summary = "Crear resena", description = "Crea una nueva resena o valoracion para un producto")
    @ApiResponse(responseCode = "201", description = "Resena creada exitosamente")
    @PostMapping
    public ResponseEntity<?> crearResena(@Valid @RequestBody Resena resena) {
        resena.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.crearResena(resena));
    }

    @Operation(summary = "Resenas por producto", description = "Obtiene todas las resenas de un producto")
    @ApiResponse(responseCode = "200", description = "Lista de resenas obtenida")
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    @Operation(summary = "Promedio de puntuacion", description = "Obtiene el promedio de puntuacion de un producto (escala 1 a 5)")
    @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente")
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedio(@PathVariable Long productoId) {
        Double promedio = resenaService.obtenerPromedio(productoId);
        return ResponseEntity.ok(Map.of(
                "productoId", productoId,
                "promedioPuntuacion", promedio,
                "escala", "1 a 5 estrellas"
        ));
    }

    @Operation(summary = "Resenas por usuario", description = "Obtiene todas las resenas escritas por un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de resenas obtenida")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }

    @Operation(summary = "Eliminar resena", description = "Elimina una resena del sistema por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resena eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (resenaService.eliminar(id)) {
            return ResponseEntity.ok("Resena eliminada correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resena no encontrada con ID: " + id);
    }
}