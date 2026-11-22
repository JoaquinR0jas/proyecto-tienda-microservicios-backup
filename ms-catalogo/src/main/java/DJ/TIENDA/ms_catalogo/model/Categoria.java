package DJ.TIENDA.ms_catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre puede tener maximo 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column
    private String descripcion; // Opcional
}
