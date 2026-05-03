package DJ.TIENDA.ms_inventario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity // Indica que esta clase es una entidad de base de datos
@Table(name = "inventarios") // Define el nombre de la tabla en MySQL
@Data // Genera Getters, Setters, ToString, etc., automáticamente (Lombok)
public class Inventario {

    @Id // Llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrementable
    private Long id;

    /**
     * IMPORTANTE: Este campo NO es una relación @ManyToOne real en la base de datos.
     * En microservicios, guardamos solo el ID del producto que vive en 'ms-catalogo'.
     * Esto se llama "Loose Coupling" (Acoplamiento débil).
     * ESTO ME LO DIJO GEMINI
     */
    private Long productoId;

    // Cantidad de unidades disponibles en stock
    private Integer cantidad;
    
}