package DJ.TIENDA.ms_pagos.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {
    private Long pagoId;
    private Long pedidoId;
    private Long usuarioId;
    private Double monto;
    private String estado;
    private String detalle;
    private LocalDateTime fechaPago;
}