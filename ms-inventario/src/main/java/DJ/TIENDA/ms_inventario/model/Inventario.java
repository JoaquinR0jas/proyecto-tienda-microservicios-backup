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
     * Ojo: este campo no es un @ManyToOne de verdad, porque el producto vive en
     * otro microservicio (ms-catalogo). Asi que guardamos nomás su id, cada servicio
     * maneja su propia base y no se enganchan relaciones entre bases.
     */
    private Long productoId;

    // Cantidad de unidades disponibles en stock
    private Integer cantidad;
    
}