package DJ.TIENDA.ms_pedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

// Lo que envia el frontend al apretar "Pagar ahora": el carrito del navegador + los datos del checkout
@Data
public class CrearPedidoDTO {

    @NotNull(message = "El usuario es obligatorio, hay que iniciar sesion para comprar")
    private Long usuarioId;

    @NotNull(message = "Los datos del cliente son obligatorios")
    @Valid
    private ClienteDTO cliente;

    @NotNull(message = "Los datos de entrega son obligatorios")
    @Valid
    private EntregaDTO entrega;

    private String medioPago; // DEBITO, CREDITO o TRANSFERENCIA

    private boolean simularRechazo; // Para mostrar "Pago con error" en la demo

    @NotEmpty(message = "El carrito esta vacio")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;
    }
}
