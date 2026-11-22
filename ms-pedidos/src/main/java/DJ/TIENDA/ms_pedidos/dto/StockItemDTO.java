package DJ.TIENDA.ms_pedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Producto y cantidad que se envian a ms-catalogo para descontar el stock
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDTO {
    private Long productoId;
    private Integer cantidad;
}
