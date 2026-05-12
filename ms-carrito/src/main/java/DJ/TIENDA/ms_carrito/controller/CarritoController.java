package DJ.TIENDA.ms_carrito.controller;

import DJ.TIENDA.ms_carrito.dto.CarritoResponseDTO;
import DJ.TIENDA.ms_carrito.model.Carrito;
import DJ.TIENDA.ms_carrito.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    // GET /api/carrito/{usuarioId} → Ver carrito activo del usuario
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> verCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.verCarrito(usuarioId));
    }

    // GET /api/carrito/detalle/{carritoId} → Ver carrito por ID (usado por ms-pedidos via Feign)
    @GetMapping("/detalle/{carritoId}")
    public ResponseEntity<?> verCarritoPorId(@PathVariable Long carritoId) {
        try {
            return ResponseEntity.ok(carritoService.verCarritoPorId(carritoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Carrito no encontrado con ID: " + carritoId);
        }
    }

    // POST /api/carrito/{usuarioId}/agregar → Agregar producto al carrito
    @PostMapping("/{usuarioId}/agregar")
    public ResponseEntity<?> agregarProducto(@PathVariable Long usuarioId,
                                              @RequestBody Map<String, Integer> body) {
        try {
            Long productoId = Long.valueOf(body.get("productoId"));
            Integer cantidad = body.get("cantidad");
            return ResponseEntity.ok(carritoService.agregarProducto(usuarioId, productoId, cantidad));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // DELETE /api/carrito/{carritoId}/item/{itemId} → Eliminar un item del carrito
    @DeleteMapping("/{carritoId}/item/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(@PathVariable Long carritoId,
                                                            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(carritoId, itemId));
    }

    // DELETE /api/carrito/{usuarioId}/vaciar → Vaciar todo el carrito
    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<CarritoResponseDTO> vaciarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioId));
    }

    // PATCH /api/carrito/{carritoId}/estado → Cambiar estado del carrito
    @PatchMapping("/{carritoId}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long carritoId,
                                            @RequestBody Map<String, String> body) {
        try {
            Carrito.Estado nuevoEstado = Carrito.Estado.valueOf(body.get("estado"));
            return ResponseEntity.ok(carritoService.cambiarEstado(carritoId, nuevoEstado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estado invalido. Usa: ACTIVO, CONFIRMADO o CANCELADO");
        }
    }
}