package DJ.TIENDA.ms_pagos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

// Lo que envia ms-pedidos para procesar el pago de un pedido
@Data
public class PagoRequestDTO {

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    private String medio; // DEBITO, CREDITO o TRANSFERENCIA (por defecto DEBITO)

    // Como no hay un banco real, el frontend puede pedir que el pago se rechace para la demo
    private boolean simularRechazo;
}
