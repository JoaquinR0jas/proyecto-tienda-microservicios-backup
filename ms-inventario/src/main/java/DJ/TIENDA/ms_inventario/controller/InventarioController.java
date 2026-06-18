package DJ.TIENDA.ms_inventario.controller;

import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService; // Ahora habla con el Service, no directo al Repository

    // GET /api/inventario/stock → Lista todo el inventario
    @GetMapping("/stock")
    public ResponseEntity<List<Inventario>> listarTodoElStock() {
        return ResponseEntity.ok(inventarioService.obtenerTodo());
    }

    // GET /api/inventario/producto/{productoId} → Stock de un producto específico
    // Esta ruta es la que usa ms-catalogo via Feign, no la cambies
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<?> verStockPorProducto(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProductoId(productoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay stock registrado para el producto con ID: " + productoId));
    }

    // POST /api/inventario/guardar → Guarda o actualiza stock de un producto
    @PostMapping("/guardar")
    public ResponseEntity<Inventario> guardarStock(@RequestBody Inventario inventario) {
        inventario.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.guardar(inventario));
    }

    // DELETE /api/inventario/{id} → Elimina un registro de inventario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (inventarioService.eliminar(id)) {
            return ResponseEntity.ok("Registro de inventario eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Registro no encontrado con ID: " + id);
    }
}