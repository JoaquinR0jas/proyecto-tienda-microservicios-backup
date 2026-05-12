package DJ.TIENDA.ms_pedidos.controller;

import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // POST /api/pedidos/crear?usuarioId=1&carritoId=2 → Crea pedido desde carritoId especifico
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

    // GET /api/pedidos/usuario/{usuarioId} → Lista todos los pedidos de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerPedidosPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.obtenerPedidosPorUsuario(usuarioId));
    }

    // GET /api/pedidos/{pedidoId} → Ver detalle de un pedido especifico
    @GetMapping("/{pedidoId}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long pedidoId) {
        return pedidoService.obtenerPorId(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pedido no encontrado con ID: " + pedidoId));
    }
}