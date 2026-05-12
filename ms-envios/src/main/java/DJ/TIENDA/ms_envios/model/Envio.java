package DJ.TIENDA.ms_envios.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId; // Referencia debil al pedido en ms-pedidos

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios

    @Column(nullable = false)
    private String direccionEntrega; // Obtenida de ms-usuarios via Feign

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column
    private LocalDateTime fechaEntrega; // Se llena cuando el estado es ENTREGADO

    public enum Estado {
        PENDIENTE,   // Envio creado, esperando despacho
        EN_CAMINO,   // Paquete en camino al cliente
        ENTREGADO,   // Paquete recibido por el cliente
        FALLIDO      // No se pudo entregar
    }
}