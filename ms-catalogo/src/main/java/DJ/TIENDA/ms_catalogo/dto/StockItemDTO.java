package DJ.TIENDA.ms_catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Un producto y la cantidad a descontar, lo envia ms-pedidos cuando un pago se aprueba
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockItemDTO {
    private Long productoId;
    private Integer cantidad;
}
