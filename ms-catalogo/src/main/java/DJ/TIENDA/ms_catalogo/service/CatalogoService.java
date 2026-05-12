package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.client.InventarioClient;
import DJ.TIENDA.ms_catalogo.dto.InventarioDTO;
import DJ.TIENDA.ms_catalogo.dto.ProductoDetalleDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Capa de lógica de negocio entre Controller y Repository
public class CatalogoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private InventarioClient inventarioClient; // Se mueve aquí desde el Controller

    // Retorna todos los productos
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Busca producto por ID y combina con stock de ms-inventario via Feign
    public Optional<ProductoDetalleDTO> obtenerDetalleConStock(Long id) {
        return productoRepository.findById(id).map(producto -> {
            // Consulta a ms-inventario via Feign
            InventarioDTO inventario = inventarioClient.obtenerStock(id);
            Integer stock = (inventario != null && inventario.getCantidad() != null)
                    ? inventario.getCantidad() : 0;

            // Construye el DTO combinando datos de ambos microservicios
            ProductoDetalleDTO detalle = new ProductoDetalleDTO();
            detalle.setId(producto.getId());
            detalle.setNombre(producto.getNombre());
            detalle.setDescripcion(producto.getDescripcion());
            detalle.setPrecio(producto.getPrecio());
            detalle.setStockDisponible(stock);

            return detalle;
        });
    }

    // Crea un producto nuevo
    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    // Elimina un producto por ID, retorna true si existía
    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Busca un producto por ID
public Optional<Producto> obtenerPorId(Long id) {
    return productoRepository.findById(id);
}
}