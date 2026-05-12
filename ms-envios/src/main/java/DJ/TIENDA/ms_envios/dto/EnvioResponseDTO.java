package DJ.TIENDA.ms_envios.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnvioResponseDTO {
    private Long envioId;
    private Long pedidoId;
    private Long usuarioId;
    private String direccionEntrega;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega;
}