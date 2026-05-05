package DJ.TIENDA.ms_catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion; // Opcional, sin validación

    @NotNull(message = "El precio es obligatorio")
    @Column(nullable = false)
    private Double precio;
}