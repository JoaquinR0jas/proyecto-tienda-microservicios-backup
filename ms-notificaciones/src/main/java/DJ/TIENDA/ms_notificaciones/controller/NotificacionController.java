package DJ.TIENDA.ms_notificaciones.controller;

import DJ.TIENDA.ms_notificaciones.dto.NotificacionResponseDTO;
import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import DJ.TIENDA.ms_notificaciones.service.NotificacionService;
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

@Tag(name = "Notificaciones", description = "Gestion de notificaciones a usuarios")
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Enviar notificacion", description = "Crea y envia una notificacion a un usuario. Tipos: PEDIDO_CREADO, PAGO_COMPLETADO, ENVIO_ENTREGADO")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notificacion enviada correctamente"),
        @ApiResponse(responseCode = "400", description = "Tipo de notificacion invalido")
    })
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarNotificacion(@RequestBody Map<String, String> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId"));
            Notificacion.Tipo tipo = Notificacion.Tipo.valueOf(body.get("tipo"));
            String mensaje = body.get("mensaje");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(notificacionService.enviarNotificacion(usuarioId, tipo, mensaje));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo invalido. Usa: PEDIDO_CREADO, PAGO_COMPLETADO o ENVIO_ENTREGADO");
        }
    }

    @Operation(summary = "Notificaciones por usuario", description = "Obtiene todas las notificaciones de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerPorUsuario(usuarioId));
    }

    @Operation(summary = "Notificaciones no leidas", description = "Obtiene las notificaciones no leidas de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leidas obtenida")
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(usuarioId));
    }

    @Operation(summary = "Marcar notificacion como leida", description = "Marca una notificacion como leida por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida"),
        @ApiResponse(responseCode = "404", description = "Notificacion no encontrada")
    })
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}