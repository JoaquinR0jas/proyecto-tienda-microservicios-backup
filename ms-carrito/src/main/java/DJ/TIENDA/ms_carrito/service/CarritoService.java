package DJ.TIENDA.ms_carrito.service;

import DJ.TIENDA.ms_carrito.client.CatalogoClient;
import DJ.TIENDA.ms_carrito.client.InventarioClient;
import DJ.TIENDA.ms_carrito.dto.CarritoResponseDTO;
import DJ.TIENDA.ms_carrito.dto.ProductoDTO;
import DJ.TIENDA.ms_carrito.dto.StockDTO;
import DJ.TIENDA.ms_carrito.model.Carrito;
import DJ.TIENDA.ms_carrito.model.CarritoItem;
import DJ.TIENDA.ms_carrito.repository.CarritoItemRepository;
import DJ.TIENDA.ms_carrito.repository.CarritoRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private CarritoItemRepository carritoItemRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private InventarioClient inventarioClient;

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevo);
                });
    }

    public CarritoResponseDTO agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        // primero verifico stock y precio de los otros microservicios para ver que onda
        StockDTO stock = inventarioClient.obtenerStock(productoId);
        if (stock == null || stock.getCantidad() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto ID: " + productoId);
        }

        ProductoDTO producto = catalogoClient.obtenerProducto(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }

        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item);
        } else {
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProductoId(productoId);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio()); // Guarda el precio actual
            carritoItemRepository.save(nuevoItem);
        }

        return construirRespuesta(carrito.getId());
    }

    @Transactional
public CarritoResponseDTO eliminarItem(Long carritoId, Long itemId) {
    Carrito carrito = carritoRepository.findById(carritoId)
            .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));
    
    // Elimina el item desde la lista del carrito, no directo al repository (asi se actualiza solo)
    carrito.getItems().removeIf(item -> item.getId().equals(itemId));
    carritoRepository.save(carrito);
    
    return construirRespuesta(carritoId);
}

    public CarritoResponseDTO vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
        return construirRespuesta(carrito.getId());
    }

    public CarritoResponseDTO cambiarEstado(Long carritoId, Carrito.Estado nuevoEstado) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));
        carrito.setEstado(nuevoEstado);
        carritoRepository.save(carrito);
        return construirRespuesta(carritoId);
    }

    public CarritoResponseDTO verCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return construirRespuesta(carrito.getId());
    }

    private CarritoResponseDTO construirRespuesta(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElseThrow();

        CarritoResponseDTO respuesta = new CarritoResponseDTO();
        respuesta.setCarritoId(carrito.getId());
        respuesta.setUsuarioId(carrito.getUsuarioId());
        respuesta.setEstado(carrito.getEstado().name());

        List<CarritoResponseDTO.ItemResponseDTO> itemsDTO = carrito.getItems().stream().map(item -> {
            CarritoResponseDTO.ItemResponseDTO itemDTO = new CarritoResponseDTO.ItemResponseDTO();
            itemDTO.setItemId(item.getId());
            itemDTO.setProductoId(item.getProductoId());
            itemDTO.setNombreProducto("Producto " + item.getProductoId());
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
            return itemDTO;
        }).toList();

        respuesta.setItems(itemsDTO);

        Double total = itemsDTO.stream().mapToDouble(CarritoResponseDTO.ItemResponseDTO::getSubtotal).sum();
        respuesta.setTotal(total);

        return respuesta;
    }
    // lo usa ms-pedidos cuando pide el carrito por id.
    public CarritoResponseDTO verCarritoPorId(Long carritoId) {
    carritoRepository.findById(carritoId)
            .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));
    return construirRespuesta(carritoId);
}
}