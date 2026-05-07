package DJ.TIENDA.ms_carrito.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion con el carrito al que pertenece este item
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @Column(nullable = false)
    private Long productoId; // Referencia debil al producto en ms-catalogo

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario; // Precio al momento de agregar, guardado localmente
}