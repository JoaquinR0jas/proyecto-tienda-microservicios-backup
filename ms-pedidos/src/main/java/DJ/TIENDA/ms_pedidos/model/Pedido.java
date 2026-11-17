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

    @Column(unique = true)
    private String numero; // Numero visible de la compra, ej: 20261117001

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios (comprar requiere sesion)

    // Copia de los datos del cliente al momento de comprar, para que la boleta no cambie
    // aunque despues el usuario edite su perfil
    @Column
    private String clienteRun;

    @Column(nullable = false)
    private String clienteNombre;

    @Column(nullable = false)
    private String clienteApellidos;

    @Column(nullable = false)
    private String clienteEmail;

    // Datos de entrega (antes estaban en ms-envios)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrega tipoEntrega = TipoEntrega.DESPACHO;

    @Column
    private String calle;

    @Column
    private String depto;

    @Column
    private Long regionId; // Referencia debil a la region en ms-usuarios

    @Column
    private Long comunaId; // Referencia debil a la comuna en ms-usuarios

    @Column(length = 500)
    private String indicaciones;

    @Column
    private String medioPago; // DEBITO, CREDITO o TRANSFERENCIA

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double costoEnvio;

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE; // Todo pedido nuevo comienza PENDIENTE

    @Column
    private String motivoRechazo; // Solo cuando el pedido queda RECHAZADO

    @Column
    private Long folioBoleta; // Folio de la boleta emitida por ms-pagos, solo cuando queda PAGADO

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now(); // Se guarda automaticamente al crear

    // Un pedido tiene muchos items, si se borra el pedido se borran sus items
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PedidoItem> items = new ArrayList<>();

    public enum TipoEntrega {
        DESPACHO,  // Despacho a domicilio
        RETIRO     // Retiro en tienda
    }

    public enum Estado {
        PENDIENTE,   // Pedido creado, esperando el resultado del pago
        PAGADO,      // Pago aprobado, se emitio boleta y se desconto stock
        RECHAZADO    // Pago rechazado o sin stock, no se cobra nada
    }
}
