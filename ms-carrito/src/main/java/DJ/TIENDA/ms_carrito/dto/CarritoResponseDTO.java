package DJ.TIENDA.ms_carrito.dto;

import lombok.Data;
import java.util.List;

// Objeto de respuesta que devolvemos al cliente con el carrito completo
@Data
public class CarritoResponseDTO {
    private Long carritoId;
    private Long usuarioId;
    private String estado;
    private List<ItemResponseDTO> items;
    private Double total; // Total calculado sumando precio x cantidad de cada item

    @Data
    public static class ItemResponseDTO {
        private Long itemId;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal; // precioUnitario x cantidad
    }
}