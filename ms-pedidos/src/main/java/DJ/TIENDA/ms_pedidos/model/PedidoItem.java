package DJ.TIENDA.ms_pedidos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pedido_items")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private Long productoId; // Referencia debil al producto en ms-catalogo

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario; // Precio guardado al momento del pedido
}