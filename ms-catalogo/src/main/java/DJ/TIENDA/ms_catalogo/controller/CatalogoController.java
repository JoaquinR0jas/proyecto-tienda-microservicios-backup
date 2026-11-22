package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.dto.StockItemDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.service.CatalogoService;
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

@Tag(name = "Catalogo", description = "Gestion del catalogo de productos y su stock")
@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @Operation(summary = "Listar productos", description = "Retorna todos los productos. Con ?soloActivos=true retorna solo los que se muestran en la tienda")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> listar(@RequestParam(defaultValue = "false") boolean soloActivos) {
        return ResponseEntity.ok(soloActivos ? catalogoService.obtenerActivos() : catalogoService.obtenerTodos());
    }

    @Operation(summary = "Listar ofertas", description = "Retorna los productos activos que tienen descuento")
    @ApiResponse(responseCode = "200", description = "Lista de ofertas obtenida")
    @GetMapping("/productos/ofertas")
    public ResponseEntity<List<Producto>> listarOfertas() {
        return ResponseEntity.ok(catalogoService.obtenerOfertas());
    }

    @Operation(summary = "Listar productos criticos", description = "Retorna los productos con stock en o bajo su stock critico")
    @ApiResponse(responseCode = "200", description = "Lista de productos criticos obtenida")
    @GetMapping("/productos/criticos")
    public ResponseEntity<List<Producto>> listarCriticos() {
        return ResponseEntity.ok(catalogoService.obtenerCriticos());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca un producto por su ID (usado tambien por ms-pedidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/productos/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return catalogoService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con ID: " + id));
    }

    @Operation(summary = "Obtener producto por codigo", description = "Busca un producto por su codigo, por ejemplo FF-001")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/productos/codigo/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        return catalogoService.obtenerPorCodigo(codigo)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con codigo: " + codigo));
    }

    @Operation(summary = "Crear un producto", description = "Agrega un nuevo producto al catalogo")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o categoria inexistente"),
        @ApiResponse(responseCode = "409", description = "El codigo ya existe")
    })
    @PostMapping("/productos")
    public ResponseEntity<?> crear(@Valid @RequestBody Producto producto) {
        producto.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.crear(producto));
        } catch (IllegalArgumentException e) {
            return respuestaError(e);
        }
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "409", description = "El codigo ya existe")
    })
    @PutMapping("/productos/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        try {
            return catalogoService.actualizar(id, producto)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Producto no encontrado con ID: " + id));
        } catch (IllegalArgumentException e) {
            return respuestaError(e);
        }
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catalogo por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (catalogoService.eliminar(id)) {
            return ResponseEntity.ok("Producto eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Producto no encontrado con ID: " + id);
    }

    @Operation(summary = "Descontar stock", description = "Descuenta el stock de los productos de una compra pagada (lo usa ms-pedidos)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Stock descontado"),
        @ApiResponse(responseCode = "400", description = "Producto inexistente o stock insuficiente")
    })
    @PostMapping("/productos/descontar-stock")
    public ResponseEntity<?> descontarStock(@RequestBody List<StockItemDTO> items) {
        try {
            catalogoService.descontarStock(items);
            return ResponseEntity.ok("Stock descontado correctamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // codigo repetido = 409, el resto (por ejemplo categoria inexistente) = 400
    private ResponseEntity<?> respuestaError(IllegalArgumentException e) {
        HttpStatus estado = e.getMessage().startsWith("Ya existe") ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(estado).body(e.getMessage());
    }
}
