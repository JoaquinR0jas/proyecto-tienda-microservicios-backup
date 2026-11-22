package DJ.TIENDA.ms_contenido.controller;

import DJ.TIENDA.ms_contenido.model.Articulo;
import DJ.TIENDA.ms_contenido.service.ArticuloService;
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

@Tag(name = "Blog", description = "Articulos del blog de la tienda")
@RestController
@RequestMapping("/api/v1/contenido/articulos")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @Operation(summary = "Listar articulos", description = "Retorna los articulos publicados. Con ?incluirBorradores=true retorna todos (panel admin)")
    @ApiResponse(responseCode = "200", description = "Lista de articulos obtenida")
    @GetMapping
    public ResponseEntity<List<Articulo>> listar(@RequestParam(defaultValue = "false") boolean incluirBorradores) {
        return ResponseEntity.ok(articuloService.obtenerArticulos(incluirBorradores));
    }

    @Operation(summary = "Obtener articulo por slug", description = "Busca un articulo por la parte de la URL, ej: detalle-1")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Articulo encontrado"),
        @ApiResponse(responseCode = "404", description = "Articulo no encontrado")
    })
    @GetMapping("/{slug}")
    public ResponseEntity<?> obtenerPorSlug(@PathVariable String slug) {
        return articuloService.obtenerPorSlug(slug)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Articulo no encontrado: " + slug));
    }

    @Operation(summary = "Crear articulo", description = "Agrega un nuevo articulo al blog")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Articulo creado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un articulo con ese slug")
    })
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Articulo articulo) {
        articulo.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(articuloService.crear(articulo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar articulo", description = "Modifica un articulo existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Articulo actualizado"),
        @ApiResponse(responseCode = "404", description = "Articulo no encontrado"),
        @ApiResponse(responseCode = "409", description = "Ya existe un articulo con ese slug")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Articulo articulo) {
        try {
            return articuloService.actualizar(id, articulo)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Articulo no encontrado con ID: " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar articulo", description = "Elimina un articulo del blog")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Articulo eliminado"),
        @ApiResponse(responseCode = "404", description = "Articulo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (articuloService.eliminar(id)) {
            return ResponseEntity.ok("Articulo eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Articulo no encontrado con ID: " + id);
    }
}
