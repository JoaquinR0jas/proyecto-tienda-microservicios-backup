package DJ.TIENDA.ms_pedidos.client;

import DJ.TIENDA.ms_pedidos.dto.PagoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-pagos")
public interface PagoClient {

    @PostMapping("/api/v1/pagos/procesar")
    PagoDTO procesarPago(@RequestBody PagoDTO solicitud);
}
