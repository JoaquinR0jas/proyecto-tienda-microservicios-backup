package DJ.TIENDA.ms_inventario.controller;

import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventario", description = "Gestion del inventario y stock de productos")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar todo el stock", description = "Retorna el listado completo del inventario")
    @ApiResponse(responseCode = "200", description = "Inventario obtenido correctamente")
    @GetMapping("/stock")
    public ResponseEntity<List<Inventario>> listarTodoElStock() {
        return ResponseEntity.ok(inventarioService.obtenerTodo());
    }

    @Operation(summary = "Ver stock por producto", description = "Obtiene el stock de un producto especifico por su ID (usado por ms-catalogo via Feign)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock encontrado"),
        @ApiResponse(responseCode = "404", description = "No hay stock registrado para el producto")
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> verStockPorProducto(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay stock registrado para el producto con ID: " + productoId));
    }

    @Operation(summary = "Guardar stock", description = "Guarda o actualiza el stock de un producto")
    @ApiResponse(responseCode = "201", description = "Stock guardado correctamente")
    @PostMapping("/guardar")
    public ResponseEntity<Inventario> guardarStock(@RequestBody Inventario inventario) {
        inventario.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.guardar(inventario));
    }

    @Operation(summary = "Eliminar registro de inventario", description = "Elimina un registro de inventario por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (inventarioService.eliminar(id)) {
            return ResponseEntity.ok("Registro de inventario eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Registro no encontrado con ID: " + id);
    }
}