package DJ.TIENDA.ms_pedidos.dto;

import lombok.Data;
import java.util.List;

// Representa el carrito que recibimos de ms-carrito via Feign
@Data
public class CarritoDTO {
    private Long carritoId;
    private Long usuarioId;
    private String estado;
    private List<ItemDTO> items;
    private Double total;

    @Data
    public static class ItemDTO {
        private Long itemId;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}