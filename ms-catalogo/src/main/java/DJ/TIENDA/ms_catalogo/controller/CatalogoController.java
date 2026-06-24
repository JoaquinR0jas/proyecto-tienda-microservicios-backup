package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.service.CatalogoService;
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

@Tag(name = "Catalogo", description = "Gestion del catalogo de productos")
@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @Operation(summary = "Listar todos los productos", description = "Retorna el listado completo del catalogo")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(catalogoService.obtenerTodos());
    }

    @Operation(summary = "Ver detalle con stock", description = "Retorna el producto junto con su stock disponible")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle del producto obtenido"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/productos/{id}/detalles")
    public ResponseEntity<?> verDetallesConStock(@PathVariable Long id) {
        return catalogoService.obtenerDetalleConStock(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con ID: " + id));
    }

    @Operation(summary = "Crear un producto", description = "Agrega un nuevo producto al catalogo")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PostMapping("/productos")
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        producto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(producto));
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto por su ID (usado internamente por otros microservicios)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return catalogoService.obtenerTodos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con ID: " + id));
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catalogo por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (catalogoService.eliminar(id)) {
            return ResponseEntity.ok("Producto eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Producto no encontrado con ID: " + id);
    }
}