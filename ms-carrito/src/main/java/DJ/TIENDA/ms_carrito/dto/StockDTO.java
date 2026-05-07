package DJ.TIENDA.ms_carrito.dto;

import lombok.Data;

// Datos que recibimos de ms-inventario via Feign
@Data
public class StockDTO {
    private Integer cantidad;
}