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
    private CatalogoClient catalogoClient; // Para obtener precio del producto

    @Autowired
    private InventarioClient inventarioClient; // Para verificar stock disponible

    // Obtiene o crea el carrito ACTIVO de un usuario
    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, Carrito.Estado.ACTIVO)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevo);
                });
    }

    // Agrega un producto al carrito verificando stock y obteniendo precio de ms-catalogo
    public CarritoResponseDTO agregarProducto(Long usuarioId, Long productoId, Integer cantidad) {
        // 1. Verifica stock en ms-inventario via Feign
        StockDTO stock = inventarioClient.obtenerStock(productoId);
        if (stock == null || stock.getCantidad() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para el producto ID: " + productoId);
        }

        // 2. Obtiene precio del producto en ms-catalogo via Feign
        ProductoDTO producto = catalogoClient.obtenerProducto(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }

        // 3. Obtiene o crea el carrito activo del usuario
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        // 4. Si el producto ya esta en el carrito, suma la cantidad
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findByCarritoIdAndProductoId(carrito.getId(), productoId);

        if (itemExistente.isPresent()) {
            CarritoItem item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            carritoItemRepository.save(item);
        } else {
            // Si no existe, crea un item nuevo
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProductoId(productoId);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecio()); // Guarda el precio actual
            carritoItemRepository.save(nuevoItem);
        }

        return construirRespuesta(carrito.getId());
    }

    // Elimina un item del carrito por su itemId
    public CarritoResponseDTO eliminarItem(Long carritoId, Long itemId) {
        carritoItemRepository.findById(itemId).ifPresent(item -> {
            if (item.getCarrito().getId().equals(carritoId)) {
                carritoItemRepository.delete(item);
            }
        });
        return construirRespuesta(carritoId);
    }

    // Vacia todos los items del carrito sin eliminarlo
    public CarritoResponseDTO vaciarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
        return construirRespuesta(carrito.getId());
    }

    // Cambia el estado del carrito (CONFIRMADO o CANCELADO)
    public CarritoResponseDTO cambiarEstado(Long carritoId, Carrito.Estado nuevoEstado) {
        Carrito carrito = carritoRepository.findById(carritoId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado con ID: " + carritoId));
        carrito.setEstado(nuevoEstado);
        carritoRepository.save(carrito);
        return construirRespuesta(carritoId);
    }

    // Ver el carrito activo de un usuario
    public CarritoResponseDTO verCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return construirRespuesta(carrito.getId());
    }

    // Construye el DTO de respuesta con el total calculado
    private CarritoResponseDTO construirRespuesta(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElseThrow();

        CarritoResponseDTO respuesta = new CarritoResponseDTO();
        respuesta.setCarritoId(carrito.getId());
        respuesta.setUsuarioId(carrito.getUsuarioId());
        respuesta.setEstado(carrito.getEstado().name());

        // Construye la lista de items con subtotal por item
        List<CarritoResponseDTO.ItemResponseDTO> itemsDTO = carrito.getItems().stream().map(item -> {
            CarritoResponseDTO.ItemResponseDTO itemDTO = new CarritoResponseDTO.ItemResponseDTO();
            itemDTO.setItemId(item.getId());
            itemDTO.setProductoId(item.getProductoId());
            itemDTO.setNombreProducto("Producto " + item.getProductoId()); // Nombre basico por ahora
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
            return itemDTO;
        }).toList();

        respuesta.setItems(itemsDTO);

        // Calcula el total sumando todos los subtotales
        Double total = itemsDTO.stream().mapToDouble(CarritoResponseDTO.ItemResponseDTO::getSubtotal).sum();
        respuesta.setTotal(total);

        return respuesta;
    }
}