package DJ.TIENDA.ms_notificaciones.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {
    private Long notificacionId;
    private Long usuarioId;
    private String tipo;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;
}