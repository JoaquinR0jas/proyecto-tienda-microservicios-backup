package DJ.TIENDA.ms_pedidos.service;

import DJ.TIENDA.ms_pedidos.client.CarritoClient;
import DJ.TIENDA.ms_pedidos.dto.CarritoDTO;
import DJ.TIENDA.ms_pedidos.dto.PedidoResponseDTO;
import DJ.TIENDA.ms_pedidos.model.Pedido;
import DJ.TIENDA.ms_pedidos.model.PedidoItem;
import DJ.TIENDA.ms_pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoClient carritoClient; // Para obtener el carrito confirmado via Feign

    // Crea un pedido a partir de un carritoId especifico
    public PedidoResponseDTO crearPedido(Long usuarioId, Long carritoId) {
        // 1. Obtiene el carrito especifico via Feign
        CarritoDTO carrito = carritoClient.obtenerCarritoPorId(carritoId);

        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio o no existe.");
        }

        if (!carrito.getEstado().equals("CONFIRMADO")) {
            throw new IllegalArgumentException("El carrito debe estar CONFIRMADO para crear un pedido.");
        }

        // 2. Crea el pedido con los datos del carrito
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setCarritoId(carritoId);
        pedido.setTotal(carrito.getTotal());

        // 3. Copia los items del carrito al pedido
        List<PedidoItem> items = carrito.getItems().stream().map(itemCarrito -> {
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProductoId(itemCarrito.getProductoId());
            item.setCantidad(itemCarrito.getCantidad());
            item.setPrecioUnitario(itemCarrito.getPrecioUnitario());
            return item;
        }).toList();

        pedido.setItems(items);
        pedidoRepository.save(pedido);

        return construirRespuesta(pedido);
    }

    // Obtiene todos los pedidos de un usuario
    public List<PedidoResponseDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Obtiene el detalle de un pedido especifico
    public Optional<PedidoResponseDTO> obtenerPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId).map(this::construirRespuesta);
    }

    // Construye el DTO de respuesta
    private PedidoResponseDTO construirRespuesta(Pedido pedido) {
        PedidoResponseDTO respuesta = new PedidoResponseDTO();
        respuesta.setPedidoId(pedido.getId());
        respuesta.setUsuarioId(pedido.getUsuarioId());
        respuesta.setCarritoId(pedido.getCarritoId());
        respuesta.setEstado(pedido.getEstado().name());
        respuesta.setTotal(pedido.getTotal());
        respuesta.setFechaCreacion(pedido.getFechaCreacion());

        List<PedidoResponseDTO.ItemResponseDTO> itemsDTO = pedido.getItems().stream().map(item -> {
            PedidoResponseDTO.ItemResponseDTO itemDTO = new PedidoResponseDTO.ItemResponseDTO();
            itemDTO.setProductoId(item.getProductoId());
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
            return itemDTO;
        }).toList();

        respuesta.setItems(itemsDTO);
        return respuesta;
    }
}