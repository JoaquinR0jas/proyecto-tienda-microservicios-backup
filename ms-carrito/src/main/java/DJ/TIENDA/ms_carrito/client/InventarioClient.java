package DJ.TIENDA.ms_carrito.client;

import DJ.TIENDA.ms_carrito.dto.StockDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Se conecta a ms-inventario via Eureka para verificar stock disponible
@FeignClient(name = "ms-inventario")
public interface InventarioClient {

    // Llama al endpoint GET /api/inventario/producto/{productoId} de ms-inventario
    @GetMapping("/api/inventario/producto/{productoId}")
    StockDTO obtenerStock(@PathVariable("productoId") Long productoId);
}