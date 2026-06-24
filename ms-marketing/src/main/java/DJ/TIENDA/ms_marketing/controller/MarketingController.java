package DJ.TIENDA.ms_marketing.controller;

import DJ.TIENDA.ms_marketing.dto.PromocionResponseDTO;
import DJ.TIENDA.ms_marketing.model.Promocion;
import DJ.TIENDA.ms_marketing.service.MarketingService;
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
import java.util.Map;

@Tag(name = "Marketing", description = "Gestion de promociones y campañas de marketing")
@RestController
@RequestMapping("/api/marketing")
public class MarketingController {

    @Autowired
    private MarketingService marketingService;

    @Operation(summary = "Crear promocion", description = "Crea una nueva promocion o campana de marketing")
    @ApiResponse(responseCode = "201", description = "Promocion creada exitosamente")
    @PostMapping("/promociones")
    public ResponseEntity<?> crearPromocion(@Valid @RequestBody Promocion promocion) {
        promocion.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(marketingService.crearPromocion(promocion));
    }

    @Operation(summary = "Listar promociones", description = "Obtiene todas las promociones registradas")
    @ApiResponse(responseCode = "200", description = "Lista de promociones obtenida")
    @GetMapping("/promociones")
    public ResponseEntity<List<PromocionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(marketingService.obtenerTodas());
    }

    @Operation(summary = "Promociones activas", description = "Obtiene solo las promociones con estado ACTIVO")
    @ApiResponse(responseCode = "200", description = "Lista de promociones activas obtenida")
    @GetMapping("/promociones/activas")
    public ResponseEntity<List<PromocionResponseDTO>> obtenerActivas() {
        return ResponseEntity.ok(marketingService.obtenerActivas());
    }

    @Operation(summary = "Obtener promocion por ID", description = "Obtiene el detalle de una promocion especifica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion encontrada"),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada")
    })
    @GetMapping("/promociones/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return marketingService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Promocion no encontrada con ID: " + id));
    }

    @Operation(summary = "Cambiar estado de promocion", description = "Cambia el estado de una promocion a ACTIVO o INACTIVO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Estado invalido")
    })
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

    @Operation(summary = "Eliminar promocion", description = "Elimina una promocion del sistema por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada")
    })
    @DeleteMapping("/promociones/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (marketingService.eliminar(id)) {
            return ResponseEntity.ok("Promocion eliminada correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Promocion no encontrada con ID: " + id);
    }
}