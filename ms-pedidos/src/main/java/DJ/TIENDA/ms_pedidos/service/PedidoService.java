package DJ.TIENDA.ms_pedidos.service;

import DJ.TIENDA.ms_pedidos.client.CatalogoClient;
import DJ.TIENDA.ms_pedidos.client.PagoClient;
import DJ.TIENDA.ms_pedidos.dto.*;
import DJ.TIENDA.ms_pedidos.model.Pedido;
import DJ.TIENDA.ms_pedidos.model.PedidoItem;
import DJ.TIENDA.ms_pedidos.repository.PedidoRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    // Mismas reglas de despacho que muestra el checkout del frontend
    static final double COSTO_DESPACHO = 3990;
    static final double DESPACHO_GRATIS_DESDE = 50000;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private PagoClient pagoClient;

    // Flujo de compra: arma el pedido con los precios reales del catalogo, cobra en ms-pagos
    // y si el pago se aprueba descuenta el stock. Siempre devuelve el pedido, PAGADO o RECHAZADO.
    public PedidoResponseDTO crearPedido(CrearPedidoDTO solicitud) {
        Pedido.TipoEntrega tipoEntrega = validarEntrega(solicitud.getEntrega());

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(solicitud.getUsuarioId());
        pedido.setClienteRun(solicitud.getCliente().getRun());
        pedido.setClienteNombre(solicitud.getCliente().getNombre());
        pedido.setClienteApellidos(solicitud.getCliente().getApellidos());
        pedido.setClienteEmail(solicitud.getCliente().getEmail());
        pedido.setTipoEntrega(tipoEntrega);
        if (tipoEntrega == Pedido.TipoEntrega.DESPACHO) {
            pedido.setCalle(solicitud.getEntrega().getCalle());
            pedido.setDepto(solicitud.getEntrega().getDepto());
            pedido.setRegionId(solicitud.getEntrega().getRegionId());
            pedido.setComunaId(solicitud.getEntrega().getComunaId());
            pedido.setIndicaciones(solicitud.getEntrega().getIndicaciones());
        }
        pedido.setMedioPago(solicitud.getMedioPago() == null ? "DEBITO" : solicitud.getMedioPago().toUpperCase());

        // los precios se toman del catalogo, nunca del navegador
        String sinStock = null;
        List<PedidoItem> items = new ArrayList<>();
        for (CrearPedidoDTO.Item itemSolicitud : solicitud.getItems()) {
            ProductoDTO producto = obtenerProducto(itemSolicitud.getProductoId());
            if (!"ACTIVO".equals(producto.getEstado())) {
                throw new IllegalArgumentException("El producto " + producto.getNombre() + " no esta disponible");
            }
            if (sinStock == null && producto.getStock() < itemSolicitud.getCantidad()) {
                sinStock = producto.getNombre();
            }
            PedidoItem item = new PedidoItem();
            item.setPedido(pedido);
            item.setProductoId(producto.getId());
            item.setCodigo(producto.getCodigo());
            item.setNombre(producto.getNombre());
            item.setImagen(producto.getImagen());
            item.setCantidad(itemSolicitud.getCantidad());
            item.setPrecioUnitario(producto.getPrecioFinal());
            items.add(item);
        }
        pedido.setItems(items);

        double subtotal = items.stream().mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad()).sum();
        double costoEnvio = calcularCostoEnvio(tipoEntrega, subtotal);
        pedido.setSubtotal(subtotal);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(subtotal + costoEnvio);

        pedidoRepository.save(pedido);
        pedido.setNumero(generarNumero(pedido));

        if (sinStock != null) {
            // si alguien compro antes y no alcanza el stock, no se intenta cobrar
            rechazar(pedido, "No hay stock suficiente de " + sinStock);
        } else {
            cobrar(pedido, solicitud.isSimularRechazo());
        }

        pedidoRepository.save(pedido);
        return construirRespuesta(pedido);
    }

    public List<PedidoResponseDTO> obtenerTodos() {
        return pedidoRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public List<PedidoResponseDTO> obtenerPedidosPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public Optional<PedidoResponseDTO> obtenerPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId).map(this::construirRespuesta);
    }

    private void cobrar(Pedido pedido, boolean simularRechazo) {
        PagoDTO solicitudPago = new PagoDTO();
        solicitudPago.setPedidoId(pedido.getId());
        solicitudPago.setUsuarioId(pedido.getUsuarioId());
        solicitudPago.setMonto(pedido.getTotal());
        solicitudPago.setMedio(pedido.getMedioPago());
        solicitudPago.setSimularRechazo(simularRechazo);

        PagoDTO pago;
        try {
            pago = pagoClient.procesarPago(solicitudPago);
        } catch (FeignException e) {
            rechazar(pedido, "No se pudo procesar el pago, intenta nuevamente");
            return;
        }

        if (pago == null || !"APROBADO".equals(pago.getEstado())) {
            rechazar(pedido, pago != null && pago.getDetalle() != null ? pago.getDetalle() : "El pago fue rechazado");
            return;
        }

        List<StockItemDTO> descuento = pedido.getItems().stream()
                .map(i -> new StockItemDTO(i.getProductoId(), i.getCantidad()))
                .toList();
        catalogoClient.descontarStock(descuento);

        pedido.setEstado(Pedido.Estado.PAGADO);
        pedido.setFolioBoleta(pago.getBoleta() != null ? pago.getBoleta().getFolio() : null);
    }

    private void rechazar(Pedido pedido, String motivo) {
        pedido.setEstado(Pedido.Estado.RECHAZADO);
        pedido.setMotivoRechazo(motivo);
    }

    private ProductoDTO obtenerProducto(Long productoId) {
        try {
            ProductoDTO producto = catalogoClient.obtenerProducto(productoId);
            if (producto == null) throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
            return producto;
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + productoId);
        }
    }

    private Pedido.TipoEntrega validarEntrega(EntregaDTO entrega) {
        Pedido.TipoEntrega tipo;
        try {
            tipo = Pedido.TipoEntrega.valueOf(entrega.getTipo().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de entrega invalido: " + entrega.getTipo());
        }
        if (tipo == Pedido.TipoEntrega.DESPACHO
                && (entrega.getCalle() == null || entrega.getCalle().isBlank()
                    || entrega.getRegionId() == null || entrega.getComunaId() == null)) {
            throw new IllegalArgumentException("Para despacho a domicilio se requiere calle, region y comuna");
        }
        return tipo;
    }

    private double calcularCostoEnvio(Pedido.TipoEntrega tipo, double subtotal) {
        if (tipo != Pedido.TipoEntrega.DESPACHO) return 0;
        return subtotal >= DESPACHO_GRATIS_DESDE ? 0 : COSTO_DESPACHO;
    }

    // Fecha de la compra + id con 3 digitos, ej: 20261117004
    private String generarNumero(Pedido pedido) {
        return pedido.getFechaCreacion().format(DateTimeFormatter.BASIC_ISO_DATE)
                + String.format("%03d", pedido.getId() == null ? 0 : pedido.getId());
    }

    private PedidoResponseDTO construirRespuesta(Pedido pedido) {
        PedidoResponseDTO respuesta = new PedidoResponseDTO();
        respuesta.setPedidoId(pedido.getId());
        respuesta.setNumero(pedido.getNumero());
        respuesta.setUsuarioId(pedido.getUsuarioId());

        ClienteDTO cliente = new ClienteDTO();
        cliente.setRun(pedido.getClienteRun());
        cliente.setNombre(pedido.getClienteNombre());
        cliente.setApellidos(pedido.getClienteApellidos());
        cliente.setEmail(pedido.getClienteEmail());
        respuesta.setCliente(cliente);

        EntregaDTO entrega = new EntregaDTO();
        entrega.setTipo(pedido.getTipoEntrega().name());
        entrega.setCalle(pedido.getCalle());
        entrega.setDepto(pedido.getDepto());
        entrega.setRegionId(pedido.getRegionId());
        entrega.setComunaId(pedido.getComunaId());
        entrega.setIndicaciones(pedido.getIndicaciones());
        respuesta.setEntrega(entrega);

        respuesta.setMedioPago(pedido.getMedioPago());
        respuesta.setEstado(pedido.getEstado().name());
        respuesta.setMotivoRechazo(pedido.getMotivoRechazo());
        respuesta.setFolioBoleta(pedido.getFolioBoleta());
        respuesta.setSubtotal(pedido.getSubtotal());
        respuesta.setCostoEnvio(pedido.getCostoEnvio());
        respuesta.setTotal(pedido.getTotal());
        respuesta.setFechaCreacion(pedido.getFechaCreacion());

        respuesta.setItems(pedido.getItems().stream().map(item -> {
            PedidoResponseDTO.ItemResponseDTO itemDTO = new PedidoResponseDTO.ItemResponseDTO();
            itemDTO.setProductoId(item.getProductoId());
            itemDTO.setCodigo(item.getCodigo());
            itemDTO.setNombre(item.getNombre());
            itemDTO.setImagen(item.getImagen());
            itemDTO.setCantidad(item.getCantidad());
            itemDTO.setPrecioUnitario(item.getPrecioUnitario());
            itemDTO.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
            return itemDTO;
        }).toList());
        return respuesta;
    }
}
