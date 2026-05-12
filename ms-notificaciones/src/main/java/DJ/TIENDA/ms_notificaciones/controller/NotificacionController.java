package DJ.TIENDA.ms_notificaciones.controller;

import DJ.TIENDA.ms_notificaciones.dto.NotificacionResponseDTO;
import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import DJ.TIENDA.ms_notificaciones.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    // POST /api/notificaciones/enviar → Crea y simula el envio de una notificacion
    // Body: { "usuarioId": 1, "tipo": "PEDIDO_CREADO", "mensaje": "Tu pedido fue creado." }
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

    // GET /api/notificaciones/usuario/{usuarioId} → Ver todas las notificaciones
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerPorUsuario(usuarioId));
    }

    // GET /api/notificaciones/usuario/{usuarioId}/no-leidas → Ver solo no leidas
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> obtenerNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.obtenerNoLeidas(usuarioId));
    }

    // PATCH /api/notificaciones/{id}/leer → Marcar notificacion como leida
    @PatchMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}