package DJ.TIENDA.ms_marketing.controller;

import DJ.TIENDA.ms_marketing.dto.PromocionResponseDTO;
import DJ.TIENDA.ms_marketing.model.Promocion;
import DJ.TIENDA.ms_marketing.service.MarketingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/marketing")
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    // POST /api/marketing/promociones → Crea una nueva promocion
    @PostMapping("/promociones")
    public ResponseEntity<?> crearPromocion(@Valid @RequestBody Promocion promocion) {
        promocion.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(marketingService.crearPromocion(promocion));
    }

    // GET /api/marketing/promociones → Lista todas las promociones
    @GetMapping("/promociones")
    public ResponseEntity<List<PromocionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(marketingService.obtenerTodas());
    }

    // GET /api/marketing/promociones/activas → Lista solo las promociones activas
    @GetMapping("/promociones/activas")
    public ResponseEntity<List<PromocionResponseDTO>> obtenerActivas() {
        return ResponseEntity.ok(marketingService.obtenerActivas());
    }

    // GET /api/marketing/promociones/{id} → Ver detalle de una promocion
    @GetMapping("/promociones/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return marketingService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Promocion no encontrada con ID: " + id));
    }

    // PATCH /api/marketing/promociones/{id}/estado → Cambia estado de la promocion
    // Body: { "estado": "INACTIVO" }
    @PatchMapping("/promociones/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                            @RequestBody Map<String, String> body) {
        try {
            Promocion.Estado nuevoEstado = Promocion.Estado.valueOf(body.get("estado"));
            return ResponseEntity.ok(marketingService.cambiarEstado(id, nuevoEstado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Estado invalido. Usa: ACTIVO o INACTIVO");
        }
    }

    // DELETE /api/marketing/promociones/{id} → Elimina una promocion
    @DeleteMapping("/promociones/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (marketingService.eliminar(id)) {
            return ResponseEntity.ok("Promocion eliminada correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Promocion no encontrada con ID: " + id);
    }
}