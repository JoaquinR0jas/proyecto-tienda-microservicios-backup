package DJ.TIENDA.ms_usuarios.controller;

import DJ.TIENDA.ms_usuarios.model.Comuna;
import DJ.TIENDA.ms_usuarios.model.Region;
import DJ.TIENDA.ms_usuarios.service.UbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ubicaciones", description = "Regiones y comunas de Chile")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @Operation(summary = "Listar regiones", description = "Retorna todas las regiones")
    @ApiResponse(responseCode = "200", description = "Lista de regiones obtenida")
    @GetMapping("/regiones")
    public ResponseEntity<List<Region>> listarRegiones() {
        return ResponseEntity.ok(ubicacionService.obtenerRegiones());
    }

    @Operation(summary = "Listar comunas", description = "Retorna todas las comunas")
    @ApiResponse(responseCode = "200", description = "Lista de comunas obtenida")
    @GetMapping("/comunas")
    public ResponseEntity<List<Comuna>> listarComunas() {
        return ResponseEntity.ok(ubicacionService.obtenerComunas());
    }

    @Operation(summary = "Comunas de una region", description = "Retorna las comunas que pertenecen a una region")
    @ApiResponse(responseCode = "200", description = "Lista de comunas obtenida")
    @GetMapping("/regiones/{regionId}/comunas")
    public ResponseEntity<List<Comuna>> listarComunasPorRegion(@PathVariable Long regionId) {
        return ResponseEntity.ok(ubicacionService.obtenerComunasPorRegion(regionId));
    }
}
