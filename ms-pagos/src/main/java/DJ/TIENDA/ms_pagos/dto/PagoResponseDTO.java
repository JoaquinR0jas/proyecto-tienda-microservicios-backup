package DJ.TIENDA.ms_pagos.dto;

import DJ.TIENDA.ms_pagos.model.Boleta;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {
    private Long pagoId;
    private Long pedidoId;
    private Long usuarioId;
    private Double monto;
    private String medio;
    private String estado;
    private String detalle;
    private LocalDateTime fechaPago;
    private Boleta boleta; // Solo viene cuando el pago fue APROBADO
}
