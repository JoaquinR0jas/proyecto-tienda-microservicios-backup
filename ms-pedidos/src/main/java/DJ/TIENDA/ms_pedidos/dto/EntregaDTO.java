package DJ.TIENDA.ms_pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// Opcion de entrega elegida en el checkout
@Data
public class EntregaDTO {

    @NotBlank(message = "El tipo de entrega es obligatorio")
    private String tipo; // DESPACHO o RETIRO

    // Obligatorios solo cuando el tipo es DESPACHO
    private String calle;
    private String depto;
    private Long regionId;
    private Long comunaId;
    private String indicaciones;
}
