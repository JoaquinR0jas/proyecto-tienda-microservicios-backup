package DJ.TIENDA.ms_pedidos.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

// Objeto de respuesta que devolvemos al frontend con el pedido completo (es lo que muestra la boleta)
@Data
public class PedidoResponseDTO {
    private Long pedidoId;
    private String numero;
    private Long usuarioId;
    private ClienteDTO cliente;
    private EntregaDTO entrega;
    private String medioPago;
    private String estado;
    private String motivoRechazo;
    private Long folioBoleta;
    private Double subtotal;
    private Double costoEnvio;
    private Double total;
    private LocalDateTime fechaCreacion;
    private List<ItemResponseDTO> items;

    @Data
    public static class ItemResponseDTO {
        private Long productoId;
        private String codigo;
        private String nombre;
        private String imagen;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
    }
}
