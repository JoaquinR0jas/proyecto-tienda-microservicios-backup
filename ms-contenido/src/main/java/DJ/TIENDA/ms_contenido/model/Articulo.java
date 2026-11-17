package DJ.TIENDA.ms_contenido.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

// Articulo del blog de la tienda
@Data
@Entity
@Table(name = "articulos")
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El slug es obligatorio")
    @Size(max = 100, message = "El slug puede tener maximo 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String slug; // Parte de la URL, ej: /blog/detalle-1

    @NotBlank(message = "El titulo es obligatorio")
    @Size(max = 150, message = "El titulo puede tener maximo 150 caracteres")
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 300, message = "El resumen puede tener maximo 300 caracteres")
    @Column(nullable = false, length = 300)
    private String resumen; // Texto corto que se ve en la tarjeta del blog

    @NotBlank(message = "El contenido es obligatorio")
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String contenido; // Texto completo, los parrafos van separados por una linea en blanco

    @Column(columnDefinition = "LONGTEXT")
    private String imagen; // Ruta dentro de /img o imagen subida (data url)

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PUBLICADO; // Solo los PUBLICADO se ven en la tienda

    public enum Estado {
        PUBLICADO,
        BORRADOR
    }
}
