package DJ.TIENDA.ms_resenas.model;
// SE LLAMA RESENA POR QUE LA ENIE DA ERROR Y NO TENGO LA LETRA EN MI TECLADO 
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId; // Referencia debil al producto en ms-catalogo

    @Column(nullable = false)
    private Long usuarioId; // Referencia debil al usuario en ms-usuarios

    @NotNull(message = "La puntuacion es obligatoria")
    @Min(value = 1, message = "La puntuacion minima es 1")
    @Max(value = 5, message = "La puntuacion maxima es 5")
    @Column(nullable = false)
    private Integer puntuacion; // 1 a 5 estrellas

    @NotBlank(message = "El comentario es obligatorio")
    @Column(nullable = false)
    private String comentario;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}