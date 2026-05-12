package DJ.TIENDA.ms_marketing.repository;

import DJ.TIENDA.ms_marketing.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    // Trae todas las promociones activas
    List<Promocion> findByEstado(Promocion.Estado estado);
}