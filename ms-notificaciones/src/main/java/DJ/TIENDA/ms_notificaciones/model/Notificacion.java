package DJ.TIENDA.ms_notificaciones.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // A quien va dirigida la notificacion

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tipo tipo; // Tipo de evento que origino la notificacion

    @Column(nullable = false)
    private String mensaje; // Contenido de la notificacion

    @Column(nullable = false)
    private Boolean leida = false; // false = no leida, true = leida

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum Tipo {
        PEDIDO_CREADO,    // Se genero un nuevo pedido
        PAGO_COMPLETADO,  // El pago fue procesado exitosamente
        ENVIO_ENTREGADO   // El paquete fue entregado
    }
}