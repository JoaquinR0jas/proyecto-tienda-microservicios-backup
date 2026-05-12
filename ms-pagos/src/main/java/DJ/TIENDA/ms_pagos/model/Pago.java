package DJ.TIENDA.ms_pagos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId; // Referencia debil al pedido en ms-pedidos

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios

    @Column(nullable = false)
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    @Column
    private String detalle; // Mensaje del resultado del pago

    @Column(nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    public enum Estado {
        PENDIENTE,   // Pago iniciado, esperando procesamiento
        COMPLETADO,  // Pago exitoso
        FALLIDO      // Pago rechazado
    }
}