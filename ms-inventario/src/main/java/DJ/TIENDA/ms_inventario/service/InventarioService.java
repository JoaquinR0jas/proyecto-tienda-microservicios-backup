package DJ.TIENDA.ms_inventario.service;

import DJ.TIENDA.ms_inventario.model.Inventario;
import DJ.TIENDA.ms_inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Capa de lógica de negocio entre Controller y Repository
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    // Retorna todo el stock de todos los productos
    public List<Inventario> obtenerTodo() {
        return inventarioRepository.findAll();
    }

    // Busca el stock de un producto específico por su productoId
    public Optional<Inventario> obtenerPorProductoId(Long productoId) {
        List<Inventario> resultados = inventarioRepository.findByProductoId(productoId);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    // Guarda o actualiza el stock de un producto
    public Inventario guardar(Inventario inventario) {
        if (inventario.getProductoId() != null) {
            List<Inventario> existentes = inventarioRepository.findByProductoId(inventario.getProductoId());
            if (!existentes.isEmpty()) {
                Inventario existente = existentes.get(0);
                existente.setCantidad(inventario.getCantidad());
                return inventarioRepository.save(existente);
            }
        }
        inventario.setId(null);
        return inventarioRepository.save(inventario);
    }

    // Elimina un registro de inventario por su ID
    public boolean eliminar(Long id) {
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}