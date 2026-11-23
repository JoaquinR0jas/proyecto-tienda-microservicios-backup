package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.CrearPedidoDTO;
import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Gestion de pedidos de compra")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/pedidos")
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida"),
        @ApiResponse(responseCode = "403", description = "Un cliente solo puede ver sus propias compras")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerPedidosPorUsuario(@PathVariable Long usuarioId,
                                                      @RequestHeader(value = "X-Usuario-Id", required = false) Long idSesion,
                                                      @RequestHeader(value = "X-Usuario-Rol", required = false) String rolSesion) {
        // El cliente solo puede pedir su propio historial; el admin ve cualquiera
        if (!"ADMIN".equals(rolSesion) && (idSesion == null || !idSesion.equals(usuarioId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver estas compras");
        }
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(usuarioId));
    }

    @Operation(summary = "Obtener pedido por ID", description = "Obtiene el detalle de un pedido especifico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long pedidoId,
                                          @RequestHeader(value = "X-Usuario-Id", required = false) Long idSesion,
                                          @RequestHeader(value = "X-Usuario-Rol", required = false) String rolSesion) {
        return pedidoService.obtenerPorId(pedidoId)
                .<ResponseEntity<?>>map(pedido -> {
                    // Admin y vendedor ven el detalle de cualquier pedido;
                    // el cliente solo el de sus propias compras
                    boolean veTodas = "ADMIN".equals(rolSesion) || "VENDEDOR".equals(rolSesion);
                    if (!veTodas
                            && (idSesion == null || !idSesion.equals(pedido.getUsuarioId()))) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("No tienes permiso para ver este pedido");
                    }
                    return ResponseEntity.ok(pedido);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado con ID: " + pedidoId));
    }
}
