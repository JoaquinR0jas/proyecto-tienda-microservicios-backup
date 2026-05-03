package DJ.TIENDA.ms_catalogo.repository;

import DJ.TIENDA.ms_catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //acceso a datos (Base de Datos)
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Al heredar de JpaRepository<Entidad, TipoDelId>, Spring Boot nos regala 
    // métodos listos para usar sin escribir SQL, como:
    // save() -> Para guardar o actualizar
    // findAll() -> Para traer todos los registros
    // findById() -> Para buscar por ID
    // deleteById() -> Para borrar
    // y muchos mas que tengo en los apuntes de esta clase 
    // y tambein en codigos anteriores asi que revisar drive si hay dudas
}