package DJ.TIENDA.ms_marketing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre; // Ej: "Black Friday", "Cyber Monday"

    @Column
    private String descripcion;

    @NotNull(message = "El descuento es obligatorio")
    @Min(value = 1, message = "El descuento minimo es 1%")
    @Max(value = 100, message = "El descuento maximo es 100%")
    @Column(nullable = false)
    private Integer descuentoPorcentaje; // Ej: 10 = 10% off

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column
    private LocalDateTime fechaExpiracion; // Opcional, puede no tener fecha limite

    public enum Estado {
        ACTIVO,   // Promocion vigente
        INACTIVO  // Promocion desactivada
    }
}