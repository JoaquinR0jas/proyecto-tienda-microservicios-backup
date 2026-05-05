package DJ.TIENDA.ms_catalogo.dto;

import lombok.Data;

@Data
public class ProductoDetalleDTO {
    // Datos que vienen de ms-catalogo (base de datos propia)
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;

    // Dato que viene de ms-inventario via Feign
    private Integer stockDisponible;
}