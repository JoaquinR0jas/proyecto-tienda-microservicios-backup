package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.CrearPedidoDTO;
import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
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

@Tag(name = "Pedidos", description = "Gestion de pedidos de compra")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Operation(summary = "Crear pedido y pagar", description = "Recibe el carrito y los datos del checkout, procesa el pago y retorna el pedido PAGADO (con folio de boleta) o RECHAZADO (con el motivo)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado, revisar el campo estado: PAGADO o RECHAZADO"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos, producto inexistente o no disponible")
    })
    @PostMapping
    public ResponseEntity<?> crearPedido(@Valid @RequestBody CrearPedidoDTO solicitud) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(solicitud));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "Listar pedidos", description = "Obtiene todos los pedidos, los mas recientes primero (panel de administracion)")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @Operation(summary = "Pedidos por usuario", description = "Obtiene el historial de compras de un usuario")
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
