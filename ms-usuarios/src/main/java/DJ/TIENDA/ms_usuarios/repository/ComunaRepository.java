package DJ.TIENDA.ms_usuarios.repository;

import DJ.TIENDA.ms_usuarios.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComunaRepository extends JpaRepository<Comuna, Long> {

    List<Comuna> findByRegionId(Long regionId); // comunas de una region, para los select del frontend
}
