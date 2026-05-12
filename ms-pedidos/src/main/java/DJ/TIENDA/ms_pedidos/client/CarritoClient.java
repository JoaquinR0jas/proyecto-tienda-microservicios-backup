package DJ.TIENDA.ms_pedidos.client;

import DJ.TIENDA.ms_pedidos.dto.CarritoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-carrito")
public interface CarritoClient {

    // Obtiene un carrito especifico por su ID
    @GetMapping("/api/carrito/detalle/{carritoId}")
    CarritoDTO obtenerCarritoPorId(@PathVariable("carritoId") Long carritoId);
}