package DJ.TIENDA.ms_pedidos.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// Objeto de respuesta que devolvemos al cliente con el pedido completo
@Data
public class PedidoResponseDTO {
    private Long pedidoId;
    private Long usuarioId;
    private Long carritoId;
    private String estado;
    private Double total;
    private LocalDateTime fechaCreacion;
    private List<ItemResponseDTO> items;

    @Data
    public static class ItemResponseDTO {
        private Long productoId;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}