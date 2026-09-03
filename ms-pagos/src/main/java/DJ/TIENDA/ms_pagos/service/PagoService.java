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

    // Simula la pasarela de pago: 80% pasa, 20% se rechaza
    public PagoResponseDTO procesarPago(Long pedidoId, Long usuarioId, Double monto) {
        Pago pago = new Pago();
        pago.setPedidoId(pedidoId);
        pago.setUsuarioId(usuarioId);
        pago.setMonto(monto);

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

    public List<PagoResponseDTO> obtenerPagosPorPedido(Long pedidoId) {
        return pagoRepository.findByPedidoId(pedidoId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public List<PagoResponseDTO> obtenerPagosPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Arma el DTO tal cual lo espera el frontend
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