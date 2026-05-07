package DJ.TIENDA.ms_carrito.client;

import DJ.TIENDA.ms_carrito.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Se conecta a ms-catalogo via Eureka para obtener datos del producto
@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {

    // Llama al endpoint GET /api/catalogo/productos/{id} de ms-catalogo
    @GetMapping("/api/catalogo/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Long id);
}