package DJ.TIENDA.ms_envios.controller;

import DJ.TIENDA.ms_envios.dto.EnvioResponseDTO;
import DJ.TIENDA.ms_envios.model.Envio;
import DJ.TIENDA.ms_envios.service.EnvioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Envios", description = "Gestion de envios y seguimiento")
@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    @Autowired
    private EnvioService envioService;

    @Operation(summary = "Crear envio", description = "Crea un envio para un pedido pagado. Body: { pedidoId, usuarioId }")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Envio creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error al crear envio (ya existe o datos invalidos)")
    })
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

    @Operation(summary = "Actualizar estado de envio", description = "Actualiza el estado de un envio: PENDIENTE, EN_CAMINO, ENTREGADO o FALLIDO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
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

    @Operation(summary = "Obtener envio por pedido", description = "Obtiene el envio asociado a un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Envio encontrado"),
        @ApiResponse(responseCode = "404", description = "No hay envio para ese pedido")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> obtenerPorPedido(@PathVariable Long pedidoId) {
        return envioService.obtenerPorPedido(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay envio para el pedido ID: " + pedidoId));
    }

    @Operation(summary = "Envios por usuario", description = "Obtiene todos los envios de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de envios obtenida")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<EnvioResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(envioService.obtenerPorUsuario(usuarioId));
    }
}