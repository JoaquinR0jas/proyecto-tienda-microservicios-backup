package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.dto.ProductoDetalleDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.service.CatalogoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService; // Ahora habla con el Service, no directo al Repository

    // GET /api/catalogo/productos → Lista todos los productos
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(catalogoService.obtenerTodos());
    }

    // GET /api/catalogo/productos/{id}/detalles → Producto + stock en JSON
    @GetMapping("/productos/{id}/detalles")
    public ResponseEntity<?> verDetallesConStock(@PathVariable Long id) {
        return catalogoService.obtenerDetalleConStock(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con ID: " + id));
    }

    // POST /api/catalogo/productos → Crea un producto nuevo
    @PostMapping("/productos")
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        // @Valid activa las validaciones de @NotBlank y @NotNull definidas en la entidad
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(producto));
    }

    // DELETE /api/catalogo/productos/{id} → Elimina un producto
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (catalogoService.eliminar(id)) {
            return ResponseEntity.ok("Producto eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Producto no encontrado con ID: " + id);
    }
}