package DJ.TIENDA.ms_resenas.controller;

import DJ.TIENDA.ms_resenas.dto.ResenaResponseDTO;
import DJ.TIENDA.ms_resenas.model.Resena;
import DJ.TIENDA.ms_resenas.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    // POST /api/resenas → Crea una nueva resena
    @PostMapping
    public ResponseEntity<?> crearResena(@Valid @RequestBody Resena resena) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.crearResena(resena));
    }

    // GET /api/resenas/producto/{productoId} → Ver resenas de un producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }

    // GET /api/resenas/producto/{productoId}/promedio → Ver promedio de puntuacion
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<Map<String, Object>> obtenerPromedio(@PathVariable Long productoId) {
        Double promedio = resenaService.obtenerPromedio(productoId);
        return ResponseEntity.ok(Map.of(
                "productoId", productoId,
                "promedioPuntuacion", promedio,
                "escala", "1 a 5 estrellas"
        ));
    }

    // GET /api/resenas/usuario/{usuarioId} → Ver resenas de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }

    // DELETE /api/resenas/{id} → Elimina una resena
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (resenaService.eliminar(id)) {
            return ResponseEntity.ok("Resena eliminada correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resena no encontrada con ID: " + id);
    }
}