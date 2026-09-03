package DJ.TIENDA.ms_marketing.repository;

import DJ.TIENDA.ms_marketing.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    // Filtrar por estado, igual que lo vimos en clase
    List<Promocion> findByEstado(Promocion.Estado estado);
}