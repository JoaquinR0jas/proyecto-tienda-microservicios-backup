package DJ.TIENDA.ms_catalogo.client;

import DJ.TIENDA.ms_catalogo.dto.InventarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// El "name" DEBE ser exactamente el nombre con el que la bodega se registró en Eureka
@FeignClient(name = "ms-inventario") 
public interface InventarioClient {

    // Copiamos la ruta exacta del controlador que hicimos en la bodega
    @GetMapping("/api/inventario/producto/{productoId}")
    InventarioDTO obtenerStock(@PathVariable("productoId") Long productoId);
}