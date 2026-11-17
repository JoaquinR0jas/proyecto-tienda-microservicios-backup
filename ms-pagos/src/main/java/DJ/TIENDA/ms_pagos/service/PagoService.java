package DJ.TIENDA.ms_pagos.service;

import DJ.TIENDA.ms_pagos.dto.PagoRequestDTO;
import DJ.TIENDA.ms_pagos.dto.PagoResponseDTO;
import DJ.TIENDA.ms_pagos.model.Boleta;
import DJ.TIENDA.ms_pagos.model.Pago;
import DJ.TIENDA.ms_pagos.repository.BoletaRepository;
import DJ.TIENDA.ms_pagos.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    static final double IVA = 0.19;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private BoletaRepository boletaRepository;

    // Simula la pasarela de pago (no tenemos un banco real):
    // se aprueba siempre, salvo que el frontend pida simular un rechazo
    @Transactional
    public PagoResponseDTO procesarPago(PagoRequestDTO solicitud) {
        Pago pago = new Pago();
        pago.setPedidoId(solicitud.getPedidoId());
        pago.setUsuarioId(solicitud.getUsuarioId());
        pago.setMonto(solicitud.getMonto());
        pago.setMedio(convertirMedio(solicitud.getMedio()));

        if (solicitud.isSimularRechazo()) {
            pago.setEstado(Pago.Estado.RECHAZADO);
            pago.setDetalle("El medio de pago fue rechazado por el banco");
            pagoRepository.save(pago);
            return construirRespuesta(pago, null);
        }

        pago.setEstado(Pago.Estado.APROBADO);
        pago.setDetalle("Pago procesado exitosamente.");
        pagoRepository.save(pago);

        Boleta boleta = emitirBoleta(pago);
        return construirRespuesta(pago, boleta);
    }

    // La boleta lleva un folio correlativo y el IVA desglosado (el total ya incluye IVA)
    private Boleta emitirBoleta(Pago pago) {
        long siguienteFolio = boletaRepository.findTopByOrderByFolioDesc()
                .map(ultima -> ultima.getFolio() + 1)
                .orElse(1L);

        double total = pago.getMonto();
        double neto = Math.round(total / (1 + IVA));

        Boleta boleta = new Boleta();
        boleta.setFolio(siguienteFolio);
        boleta.setPagoId(pago.getId());
        boleta.setPedidoId(pago.getPedidoId());
        boleta.setUsuarioId(pago.getUsuarioId());
        boleta.setNeto(neto);
        boleta.setIva(total - neto);
        boleta.setTotal(total);
        return boletaRepository.save(boleta);
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

    public List<Boleta> obtenerBoletas() {
        return boletaRepository.findAll();
    }

    public Optional<Boleta> obtenerBoletaPorFolio(Long folio) {
        return boletaRepository.findByFolio(folio);
    }

    public List<Boleta> obtenerBoletasPorPedido(Long pedidoId) {
        return boletaRepository.findByPedidoId(pedidoId);
    }

    private Pago.Medio convertirMedio(String medio) {
        if (medio == null || medio.isBlank()) return Pago.Medio.DEBITO;
        try {
            return Pago.Medio.valueOf(medio.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Medio de pago invalido: " + medio);
        }
    }

    private PagoResponseDTO construirRespuesta(Pago pago) {
        return construirRespuesta(pago, boletaRepository.findByPagoId(pago.getId()).orElse(null));
    }

    // Arma el DTO tal cual lo espera el frontend
    private PagoResponseDTO construirRespuesta(Pago pago, Boleta boleta) {
        PagoResponseDTO respuesta = new PagoResponseDTO();
        respuesta.setPagoId(pago.getId());
        respuesta.setPedidoId(pago.getPedidoId());
        respuesta.setUsuarioId(pago.getUsuarioId());
        respuesta.setMonto(pago.getMonto());
        respuesta.setMedio(pago.getMedio().name());
        respuesta.setEstado(pago.getEstado().name());
        respuesta.setDetalle(pago.getDetalle());
        respuesta.setFechaPago(pago.getFechaPago());
        respuesta.setBoleta(boleta);
        return respuesta;
    }
}
