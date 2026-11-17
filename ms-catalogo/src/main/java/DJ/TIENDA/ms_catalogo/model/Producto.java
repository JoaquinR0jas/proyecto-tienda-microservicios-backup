package DJ.TIENDA.ms_catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    @Size(min = 3, message = "El codigo debe tener minimo 3 caracteres")
    @Column(nullable = false, unique = true)
    private String codigo; // Ej: FF-001, es el que se ve en la tienda y en el admin

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre puede tener maximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripcion puede tener maximo 500 caracteres")
    @Column(length = 500)
    private String descripcion; // Opcional

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double precio;

    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 90, message = "El descuento maximo es 90%")
    @Column(nullable = false)
    private Integer descuento = 0; // Porcentaje, si es mayor a 0 el producto sale en Ofertas

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock; // Antes estaba en ms-inventario, ahora vive junto al producto

    @Min(value = 0, message = "El stock critico no puede ser negativo")
    @Column
    private Integer stockCritico; // Opcional, avisa en el admin cuando el stock llega a este nivel

    @NotNull(message = "La categoria es obligatoria")
    @Column(nullable = false)
    private Long categoriaId; // Referencia a la tabla categorias de este mismo microservicio

    @Column(columnDefinition = "LONGTEXT")
    private String imagen; // Ruta dentro de /img o imagen subida desde el computador (data url)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO; // Solo los ACTIVO se muestran en la tienda

    @Column(nullable = false)
    private Boolean destacado = false; // Aparece en el carrusel del inicio

    public enum Estado {
        ACTIVO,
        INACTIVO
    }

    // Precio con el descuento aplicado, se calcula y no se guarda en la base de datos
    public Double getPrecioFinal() {
        if (precio == null) return null;
        int d = descuento == null ? 0 : descuento;
        return (double) Math.round(precio * (100 - d) / 100);
    }

    // true cuando el stock esta en o bajo el stock critico
    public boolean isCritico() {
        return stockCritico != null && stock != null && stock <= stockCritico;
    }
}
