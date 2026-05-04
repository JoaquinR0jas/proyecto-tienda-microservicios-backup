package DJ.TIENDA.ms_inventario.controller;

import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario") // link o ruta base pa este ms
public class InventarioController {

    @Autowired
    private InventarioRepository inventarioRepository;

    // 1. Ver todo el inventario de todas las bodegas
    // GET http://localhost:8080/api/inventario/stock
    @GetMapping("/stock")
    public List<Inventario> listarTodoElStock() {
        return inventarioRepository.findAll();
    }

    // 2. Ver el stock de UN SOLO producto en específico y esto se hace a partir del id de este producto
    // GET http://localhost:8080/api/inventario/producto/1
    @GetMapping("/producto/{productoId}")
    public Optional<Inventario> verStockPorProducto(@PathVariable Long productoId) {
        // Aquí uso el metodo que cree en el repo
        return inventarioRepository.findByProductoId(productoId);
    }

    // 3. Guardar o actualizar stock 
    // POST http://localhost:8080/api/inventario/guardar 
    @PostMapping("/guardar")
    public Inventario guardarStock(@RequestBody Inventario inventario) {
        return inventarioRepository.save(inventario);
    }
}