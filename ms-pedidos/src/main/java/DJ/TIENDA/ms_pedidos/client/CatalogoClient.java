package DJ.TIENDA.ms_pedidos.client;

import DJ.TIENDA.ms_pedidos.dto.ProductoDTO;
import DJ.TIENDA.ms_pedidos.dto.StockItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ms-catalogo")
public interface CatalogoClient {

    @GetMapping("/api/v1/catalogo/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable("id") Long id);

    @PostMapping("/api/v1/catalogo/productos/descontar-stock")
    String descontarStock(@RequestBody List<StockItemDTO> items);
}
