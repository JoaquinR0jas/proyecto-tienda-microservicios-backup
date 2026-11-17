package DJ.TIENDA.ms_usuarios.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comunas")
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long regionId; // Region a la que pertenece la comuna

    @Column(nullable = false)
    private String nombre;
}
