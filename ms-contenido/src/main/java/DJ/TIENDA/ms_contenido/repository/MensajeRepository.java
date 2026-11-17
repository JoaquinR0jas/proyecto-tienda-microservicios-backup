package DJ.TIENDA.ms_contenido.repository;

import DJ.TIENDA.ms_contenido.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findAllByOrderByFechaDesc(); // los mas recientes primero, como en el panel admin
}
