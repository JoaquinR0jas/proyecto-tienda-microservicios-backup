package DJ.TIENDA.ms_catalogo.controller;

import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Le dice a Spring que esta clase es un API REST y devolverá datos (JSON), no vistas/HTML.
@RequestMapping("/api/catalogo") // La ruta base. Debe coincidir con lo que configuramos en el API Gateway.
public class CatalogoController {

    @Autowired // Inyecta automáticamente el repositorio que creamos antes para poder usar sus métodos.
    private ProductoRepository productoRepository;

    // Endpoint para obtener todos los productos
    // Se accede con GET a: http://localhost:8080/api/catalogo/productos (a través del Gateway)
    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productoRepository.findAll(); // Busca todos los registros en la base de datos y los devuelve
    }

    // Endpoint para guardar un nuevo producto
    // Se accede con POST a: http://localhost:8080/api/catalogo/productos
    @PostMapping("/productos")
    public Producto guardarProducto(@RequestBody Producto producto) {
        // @RequestBody indica que los datos del producto vienen en el cuerpo de la petición (en formato JSON)
        return productoRepository.save(producto); // Guarda en la BD y devuelve el producto con su nuevo ID
    }
}