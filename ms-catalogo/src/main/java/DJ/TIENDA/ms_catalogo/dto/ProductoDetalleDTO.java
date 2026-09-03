package DJ.TIENDA.ms_catalogo.dto;

import lombok.Data;

@Data
public class ProductoDetalleDTO {
    // Datos que vienen de la base de datos propia de ms-catalogo
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;

    // Este campo lo rellena el stock que nos manda elinventario via feign(la comunicacion simple)
    private Integer stockDisponible;
}