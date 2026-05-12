package DJ.TIENDA.ms_envios.controller;

import DJ.TIENDA.ms_envios.dto.EnvioResponseDTO;
import DJ.TIENDA.ms_envios.model.Envio;
import DJ.TIENDA.ms_envios.service.EnvioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    // POST /api/envios/crear → Crea un envio para un pedido pagado
    // Body: { "pedidoId": 1, "usuarioId": 1 }
    @PostMapping("/crear")
    public ResponseEntity<?> crearEnvio(@RequestBody Map<String, Long> body) {
        try {
            Long pedidoId = body.get("pedidoId");
            Long usuarioId = body.get("usuarioId");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(envioService.crearEnvio(pedidoId, usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // PATCH /api/envios/{envioId}/estado → Actualiza estado del envio
    // Body: { "estado": "EN_CAMINO" }
    @PatchMapping("/{envioId}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long envioId,
                                               @RequestBody Map<String, String> body) {
        try {
            Envio.Estado nuevoEstado = Envio.Estado.valueOf(body.get("estado"));
            return ResponseEntity.ok(envioService.actualizarEstado(envioId, nuevoEstado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estado invalido. Usa: PENDIENTE, EN_CAMINO, ENTREGADO o FALLIDO");
        }
    }

    // GET /api/envios/pedido/{pedidoId} → Ver envio de un pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> obtenerPorPedido(@PathVariable Long pedidoId) {
        return envioService.obtenerPorPedido(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay envio para el pedido ID: " + pedidoId));
    }

    // GET /api/envios/usuario/{usuarioId} → Ver todos los envios de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EnvioResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(envioService.obtenerPorUsuario(usuarioId));
    }
}