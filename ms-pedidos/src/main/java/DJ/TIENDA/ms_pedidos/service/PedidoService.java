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
    private CarritoClient carritoClient;

    public PedidoResponseDTO crearPedido(Long usuarioId, Long carritoId) {
        // armo el pedido con lo que trajo el carrito confirmado (o sea cuando ya se pago)
        CarritoDTO carrito = carritoClient.obtenerCarritoPorId(carritoId);

        if (carrito == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito esta vacio o no existe.");
        }

        if (!carrito.getEstado().equals("CONFIRMADO")) {
            throw new IllegalArgumentException("El carrito debe estar CONFIRMADO para crear un pedido.");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setCarritoId(carritoId);
        pedido.setTotal(carrito.getTotal());

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

    public List<PedidoResponseDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public Optional<PedidoResponseDTO> obtenerPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId).map(this::construirRespuesta);
    }

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