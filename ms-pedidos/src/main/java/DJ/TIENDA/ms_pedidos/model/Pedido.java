package DJ.TIENDA.ms_pedidos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios

    @Column(nullable = false)
    private Long carritoId; // Referencia debil al carrito que origino este pedido

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE; // Todo pedido nuevo comienza PENDIENTE

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now(); // Se guarda automaticamente al crear

    // Un pedido tiene muchos items, si se borra el pedido se borran sus items
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PedidoItem> items = new ArrayList<>();

    public enum Estado {
        PENDIENTE,   // Pedido creado, esperando pago
        PAGADO,      // Pago confirmado
        ENVIADO,     // En camino al cliente
        ENTREGADO,   // Recibido por el cliente
        CANCELADO    // Pedido cancelado
    }
}