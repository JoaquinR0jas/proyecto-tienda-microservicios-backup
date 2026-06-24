package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestion de pedidos de compra")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido a partir de un carrito confirmado. Usa query params: ?usuarioId=&carritoId=")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al crear pedido (carrito no confirmado, carrito vacio)")
    })
    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestParam Long usuarioId,
                                          @RequestParam Long carritoId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(pedidoService.crearPedido(usuarioId, carritoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Pedidos por usuario", description = "Obtiene todos los pedidos de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(usuarioId));
    }

    @Operation(summary = "Obtener pedido por ID", description = "Obtiene el detalle de un pedido especifico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long pedidoId) {
        return pedidoService.obtenerPorId(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado con ID: " + pedidoId));
    }
}