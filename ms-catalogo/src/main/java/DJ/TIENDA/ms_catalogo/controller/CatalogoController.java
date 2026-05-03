package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.client.InventarioClient; // <-- IMPORTANTE
import DJ.TIENDA.ms_catalogo.dto.InventarioDTO;     // <-- IMPORTANTE
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Inyectamos nuestro "teléfono interno" para hablar con el microservicio de inventario
    @Autowired
    private InventarioClient inventarioClient;

    // 1. Endpoint clásico: Solo devuelve datos del catálogo
    @GetMapping("/productos")
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    // 2. EL PASO FINAL: Endpoint que combina datos de dos microservicios
    // GET http://localhost:8080/api/catalogo/productos/{id}/detalles
    @GetMapping("/productos/{id}/detalles")
    public String verDetallesConStock(@PathVariable Long id) {
        // A. Buscamos el producto en nuestra propia base de datos (Catálogo)
        Producto producto = productoRepository.findById(id).orElse(null);
        
        if (producto == null) {
            return "Error: El producto con ID " + id + " no existe en el catálogo.";
        }
        
        // B. LLAMADA MÁGICA: Le preguntamos a la bodega cuánto stock hay por el ID del producto
        // OpenFeign hará todo el trabajo de buscar a 'ms-inventario' en Eureka y traer el dato
        InventarioDTO inventario = inventarioClient.obtenerStock(id);
        
        // C. Preparamos la respuesta final combinando ambos mundos
        Integer stockActual = (inventario != null) ? inventario.getCantidad() : 0;
        
        return "--- FICHA DEL PRODUCTO ---\n" +
               "Nombre: " + producto.getNombre() + "\n" +
               "Descripción: " + producto.getDescripcion() + "\n" +
               "Precio: $" + producto.getPrecio() + "\n" +
               "STOCK DISPONIBLE EN BODEGA: " + stockActual + " unidades.";
    }
}