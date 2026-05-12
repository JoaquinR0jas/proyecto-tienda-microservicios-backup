package DJ.TIENDA.ms_marketing.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PromocionResponseDTO {
    private Long promocionId;
    private String nombre;
    private String descripcion;
    private Integer descuentoPorcentaje;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaExpiracion;
}