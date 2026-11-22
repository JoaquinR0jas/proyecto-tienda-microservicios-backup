package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.model.Categoria;
import DJ.TIENDA.ms_catalogo.service.CategoriaService;
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

@Tag(name = "Categorias", description = "Gestion de las categorias de productos")
@RestController
@RequestMapping("/api/v1/catalogo/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Listar categorias", description = "Retorna todas las categorias")
    @ApiResponse(responseCode = "200", description = "Lista de categorias obtenida")
    @GetMapping
    public ResponseEntity<List<Categoria>> listar() {
        return ResponseEntity.ok(categoriaService.obtenerTodas());
    }

    @Operation(summary = "Obtener categoria por ID", description = "Busca una categoria por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return categoriaService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Categoria no encontrada con ID: " + id));
    }

    @Operation(summary = "Crear categoria", description = "Agrega una nueva categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Categoria creada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoria con ese nombre")
    })
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Categoria categoria) {
        categoria.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(categoria));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar categoria", description = "Cambia el nombre o la descripcion de una categoria")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria actualizada"),
        @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
        @ApiResponse(responseCode = "409", description = "Ya existe una categoria con ese nombre")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Categoria categoria) {
        try {
            return categoriaService.actualizar(id, categoria)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Categoria no encontrada con ID: " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria que no tenga productos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria eliminada"),
        @ApiResponse(responseCode = "404", description = "Categoria no encontrada"),
        @ApiResponse(responseCode = "409", description = "La categoria tiene productos")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (categoriaService.eliminar(id)) {
                return ResponseEntity.ok("Categoria eliminada correctamente.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Categoria no encontrada con ID: " + id);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
