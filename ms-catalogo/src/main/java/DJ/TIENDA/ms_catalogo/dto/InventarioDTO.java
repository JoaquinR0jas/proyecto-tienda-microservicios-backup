package DJ.TIENDA.ms_catalogo.dto;

import lombok.Data;

@Data
public class InventarioDTO {
    // Solo nos interesa saber la cantidad de stock que nos responderá la bodega
    private Integer cantidad; 
}