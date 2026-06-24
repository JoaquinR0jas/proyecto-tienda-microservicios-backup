package DJ.TIENDA.ms_carrito.controller;

import DJ.TIENDA.ms_carrito.dto.CarritoResponseDTO;
import DJ.TIENDA.ms_carrito.model.Carrito;
import DJ.TIENDA.ms_carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Carrito", description = "Gestion del carrito de compras")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Ver carrito activo", description = "Obtiene el carrito activo de un usuario")
    @ApiResponse(responseCode = "200", description = "Carrito obtenido correctamente")
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> verCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.verCarrito(usuarioId));
    }

    @Operation(summary = "Ver carrito por ID", description = "Obtiene un carrito por su ID (usado por ms-pedidos via Feign)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito encontrado"),
        @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @GetMapping("/detalle/{carritoId}")
    public ResponseEntity<?> verCarritoPorId(@PathVariable Long carritoId) {
        try {
            return ResponseEntity.ok(carritoService.verCarritoPorId(carritoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Carrito no encontrado con ID: " + carritoId);
        }
    }

    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto con cantidad al carrito del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto agregado al carrito"),
        @ApiResponse(responseCode = "400", description = "Error al agregar producto (stock insuficiente, producto no existe)")
    })
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

    @Operation(summary = "Eliminar item del carrito", description = "Elimina un producto especifico del carrito")
    @ApiResponse(responseCode = "200", description = "Item eliminado del carrito")
    @DeleteMapping("/{carritoId}/item/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(@PathVariable Long carritoId,
                                                            @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(carritoId, itemId));
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los items del carrito de un usuario")
    @ApiResponse(responseCode = "200", description = "Carrito vaciado correctamente")
    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<CarritoResponseDTO> vaciarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioId));
    }

    @Operation(summary = "Cambiar estado del carrito", description = "Cambia el estado del carrito a ACTIVO, CONFIRMADO o CANCELADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
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