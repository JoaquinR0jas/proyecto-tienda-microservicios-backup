package DJ.TIENDA.ms_usuarios.service;

import DJ.TIENDA.ms_usuarios.model.Comuna;
import DJ.TIENDA.ms_usuarios.model.Region;
import DJ.TIENDA.ms_usuarios.repository.ComunaRepository;
import DJ.TIENDA.ms_usuarios.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // regiones y comunas para los formularios de registro, usuario y checkout
public class UbicacionService {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    public List<Region> obtenerRegiones() {
        return regionRepository.findAll();
    }

    public List<Comuna> obtenerComunas() {
        return comunaRepository.findAll();
    }

    public List<Comuna> obtenerComunasPorRegion(Long regionId) {
        return comunaRepository.findByRegionId(regionId);
    }
}
