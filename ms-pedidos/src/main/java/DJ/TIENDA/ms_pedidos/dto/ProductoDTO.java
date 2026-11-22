package DJ.TIENDA.ms_pedidos.dto;

import lombok.Data;

// Producto que recibimos de ms-catalogo via Feign (solo los campos que usa el pedido)
@Data
public class ProductoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String imagen;
    private Double precioFinal; // Precio con el descuento aplicado
    private Integer stock;
    private String estado;      // ACTIVO o INACTIVO
}
