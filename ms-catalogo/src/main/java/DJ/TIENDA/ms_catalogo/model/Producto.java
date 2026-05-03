package DJ.TIENDA.ms_catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity // Indica a Spring que esta clase representa una tabla en la base de datos.
@Table(name = "productos") // Define el nombre exacto que tendrá la tabla en MySQL.
@Data // Magia de Lombok: Genera automáticamente Getters, Setters y constructores por detrás.
public class Producto {
    
    @Id // Marca este campo como la Llave Primaria (Primary Key).
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Hace que el ID sea Autoincrementable en MySQL (1, 2, 3...).
    private Long id;
    
    // Estos atributos se convertirán en columnas dentro de la tabla "productos"
    private String nombre;
    private String descripcion;
    private Double precio;
}