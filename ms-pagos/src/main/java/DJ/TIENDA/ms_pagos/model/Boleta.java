package DJ.TIENDA.ms_pagos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

// Documento que se emite solo cuando un pago se aprueba
@Data
@Entity
@Table(name = "boletas")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long folio; // Numero correlativo de la boleta, distinto del numero de pedido

    @Column(nullable = false, unique = true)
    private Long pagoId; // Una boleta por cada pago aprobado

    @Column(nullable = false)
    private Long pedidoId; // Referencia debil al pedido en ms-pedidos

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios

    @Column(nullable = false)
    private Double neto; // Total sin IVA

    @Column(nullable = false)
    private Double iva; // 19% incluido en el total

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();
}
