package DJ.TIENDA.ms_pagos.service;

import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Pago;
import DJ.TIENDA.ms_pagos.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    // Procesa el pago simulando una pasarela real con 80% exito y 20% fallo
    public PagoResponseDTO procesarPago(Long pedidoId, Long usuarioId, Double monto) {
        Pago pago = new Pago();
        pago.setPedidoId(pedidoId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(monto);

        // Simulacion: 80% probabilidad de exito
        boolean exitoso = new Random().nextInt(10) < 8;

        if (exitoso) {
            pago.setEstado(Pago.Estado.COMPLETADO);
            pago.setDetalle("Pago procesado exitosamente.");
        } else {
            pago.setEstado(Pago.Estado.FALLIDO);
            pago.setDetalle("Pago rechazado. Fondos insuficientes.");
        }

        pagoRepository.save(pago);
        return construirRespuesta(pago);
    }

    // Obtiene todos los pagos de un pedido
    public List<PagoResponseDTO> obtenerPagosPorPedido(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Obtiene todos los pagos de un usuario
    public List<PagoResponseDTO> obtenerPagosPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Construye el DTO de respuesta
    private PagoResponseDTO construirRespuesta(Pago pago) {
        PagoResponseDTO respuesta = new PagoResponseDTO();
        respuesta.setPagoId(pago.getId());
        respuesta.setPedidoId(pago.getPedidoId());
        respuesta.setUsuarioId(pago.getUsuarioId());
        respuesta.setMonto(pago.getMonto());
        respuesta.setEstado(pago.getEstado().name());
        respuesta.setDetalle(pago.getDetalle());
        respuesta.setFechaPago(pago.getFechaPago());
        return respuesta;
    }
}