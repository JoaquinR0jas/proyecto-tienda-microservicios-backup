package DJ.TIENDA.ms_pagos.repository;

import DJ.TIENDA.ms_pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Trae todos los pagos de un pedido especifico
    List<Pago> findByPedidoId(Long pedidoId);

    // Trae todos los pagos de un usuario
    List<Pago> findByUsuarioId(Long usuarioId);
}