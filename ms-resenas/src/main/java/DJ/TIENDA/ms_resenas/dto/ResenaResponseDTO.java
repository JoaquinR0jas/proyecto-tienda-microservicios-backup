package DJ.TIENDA.ms_resenas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaResponseDTO {
    private Long resenaId;
    private Long productoId;
    private Long usuarioId;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}