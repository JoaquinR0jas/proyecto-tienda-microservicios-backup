package DJ.TIENDA.ms_carrito.dto;

import lombok.Data;

// Datos que recibimos de ms-catalogo via Feign
@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
}