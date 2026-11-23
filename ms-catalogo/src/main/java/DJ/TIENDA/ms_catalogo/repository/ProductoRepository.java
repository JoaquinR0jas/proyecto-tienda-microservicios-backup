package DJ.TIENDA.ms_catalogo.repository;

import DJ.TIENDA.ms_catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository //acceso a datos (Base de Datos)
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Al heredar de JpaRepository<Entidad, TipoDelId>, Spring Boot nos regala
    // métodos listos para usar sin escribir SQL, como:
    // save() -> Para guardar o actualizar
    // findAll() -> Para traer todos los registros
    // findById() -> Para buscar por ID
    // deleteById() -> Para borrar

    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);           // valida codigo unico antes de guardar
    boolean existsByCategoriaId(Long categoriaId);   // no se borra una categoria que tiene productos
    List<Producto> findByEstado(Producto.Estado estado);
    List<Producto> findByEstadoAndDescuentoGreaterThan(Producto.Estado estado, Integer descuento);

    // Borra en una sola consulta los productos de versiones antiguas, que quedaron
    // sin codigo ni imagen. Se hace asi para que no pasen por las validaciones del modelo.
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Query(
        "delete from Producto p where p.codigo is null or trim(p.codigo) = ''")
    int borrarProductosSinCodigo();
}
