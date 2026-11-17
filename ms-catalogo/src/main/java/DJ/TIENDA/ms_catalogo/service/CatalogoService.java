package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.dto.StockItemDTO;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.CategoriaRepository;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service // Capa de lógica de negocio entre Controller y Repository
public class CatalogoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // Lo que ve el cliente en la tienda
    public List<Producto> obtenerActivos() {
        return productoRepository.findByEstado(Producto.Estado.ACTIVO);
    }

    // Productos activos con descuento, para la vista Ofertas
    public List<Producto> obtenerOfertas() {
        return productoRepository.findByEstadoAndDescuentoGreaterThan(Producto.Estado.ACTIVO, 0);
    }

    // Productos en o bajo su stock critico, para el listado del admin
    public List<Producto> obtenerCriticos() {
        return productoRepository.findAll().stream().filter(Producto::isCritico).toList();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> obtenerPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    public Producto crear(Producto producto) {
        if (productoRepository.existsByCodigo(producto.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto con el codigo: " + producto.getCodigo());
        }
        validarCategoria(producto.getCategoriaId());
        return productoRepository.save(producto);
    }

    public Optional<Producto> actualizar(Long id, Producto datos) {
        return productoRepository.findById(id).map(existente -> {
            if (!existente.getCodigo().equals(datos.getCodigo()) && productoRepository.existsByCodigo(datos.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un producto con el codigo: " + datos.getCodigo());
            }
            validarCategoria(datos.getCategoriaId());
            existente.setCodigo(datos.getCodigo());
            existente.setNombre(datos.getNombre());
            existente.setDescripcion(datos.getDescripcion());
            existente.setPrecio(datos.getPrecio());
            existente.setDescuento(datos.getDescuento() == null ? 0 : datos.getDescuento());
            existente.setStock(datos.getStock());
            existente.setStockCritico(datos.getStockCritico());
            existente.setCategoriaId(datos.getCategoriaId());
            existente.setImagen(datos.getImagen());
            if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
            existente.setDestacado(Boolean.TRUE.equals(datos.getDestacado()));
            return productoRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Descuenta el stock de todos los productos de una compra.
    // Primero revisa que alcance para todos, asi no queda un pedido descontado a medias.
    @Transactional
    public void descontarStock(List<StockItemDTO> items) {
        for (StockItemDTO item : items) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + item.getProductoId()));
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("No hay stock suficiente de " + producto.getNombre());
            }
        }
        for (StockItemDTO item : items) {
            Producto producto = productoRepository.findById(item.getProductoId()).orElseThrow();
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }
    }

    private void validarCategoria(Long categoriaId) {
        if (categoriaId == null || !categoriaRepository.existsById(categoriaId)) {
            throw new IllegalArgumentException("La categoria no existe: " + categoriaId);
        }
    }
}
