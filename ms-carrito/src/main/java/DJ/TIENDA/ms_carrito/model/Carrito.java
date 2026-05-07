package DJ.TIENDA.ms_carrito.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario, igual que productoId en inventario

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO; // Por defecto todo carrito nuevo es ACTIVO

    // Un carrito tiene muchos items, si se borra el carrito se borran sus items
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarritoItem> items = new ArrayList<>();

    public enum Estado {
        ACTIVO,      // Carrito en uso, el usuario puede agregar/quitar productos
        CONFIRMADO,  // Carrito confirmado, se convirtio en pedido
        CANCELADO    // Carrito cancelado
    }
}