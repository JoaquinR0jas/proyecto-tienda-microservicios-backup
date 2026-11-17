package DJ.TIENDA.ms_pagos.repository;

import DJ.TIENDA.ms_pagos.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    Optional<Boleta> findTopByOrderByFolioDesc(); // la ultima boleta, para calcular el siguiente folio

    Optional<Boleta> findByFolio(Long folio);

    Optional<Boleta> findByPagoId(Long pagoId);

    List<Boleta> findByPedidoId(Long pedidoId);
}
