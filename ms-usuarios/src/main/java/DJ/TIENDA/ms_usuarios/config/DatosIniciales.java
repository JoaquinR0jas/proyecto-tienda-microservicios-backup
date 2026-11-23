package DJ.TIENDA.ms_usuarios.config;

import DJ.TIENDA.ms_usuarios.model.Comuna;
import DJ.TIENDA.ms_usuarios.model.Region;
import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.repository.ComunaRepository;
import DJ.TIENDA.ms_usuarios.repository.RegionRepository;
import DJ.TIENDA.ms_usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Carga los mismos datos de prueba que tenia el frontend en localStorage,
// solo la primera vez (cuando las tablas estan vacias)
@Component
public class DatosIniciales implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DatosIniciales(RegionRepository regionRepository, ComunaRepository comunaRepository,
                          UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (regionRepository.count() == 0) {
            Region metropolitana = region("Region Metropolitana");
            Region valparaiso = region("Valparaiso");
            Region biobio = region("Biobio");

            comuna(metropolitana, "Santiago");
            comuna(metropolitana, "Providencia");
            comuna(metropolitana, "Maipu");
            comuna(valparaiso, "Valparaiso");
            comuna(valparaiso, "Vina del Mar");
            comuna(biobio, "Concepcion");
            comuna(biobio, "Talcahuano");
        }

        // Si quedaron usuarios de pruebas antiguas con la clave en texto plano,
        // la encriptamos ahora para que puedan iniciar sesion igual.
        // Las filas incompletas (sin run, apellidos o direccion) se borran primero,
        // porque no cumplen las validaciones del modelo y no dejan arrancar el servicio.
        usuarioRepository.borrarUsuariosIncompletos();
        for (Usuario u : usuarioRepository.findAll()) {
            if (u.getPassword() != null && !u.getPassword().startsWith("$2")) {
                u.setPassword(passwordEncoder.encode(u.getPassword()));
                usuarioRepository.save(u);
            }
        }

        // Las cuentas de prueba se crean solo si todavia no existen.
        // Asi no se duplican si la base ya tenia datos de una corrida anterior.
        crearSiFalta("123456785", "Admin", "FreshFits", "admin@gmail.com", "admin123", "1990-01-01", 1L, 1L, "Av. Principal 123", Usuario.Rol.ADMIN);
        crearSiFalta("987654325", "Vendedor", "FreshFits", "vendedor@gmail.com", "vend1234", "1992-05-14", 1L, 2L, "Calle Venta 456", Usuario.Rol.VENDEDOR);
        crearSiFalta("112223339", "Camila", "Soto Perez", "camila@gmail.com", "cliente1", "1998-03-22", 2L, 4L, "Los Aromos 789", Usuario.Rol.CLIENTE);
        crearSiFalta("201113334", "Diego", "Fernandez Rojas", "diego@duoc.cl", "diego123", "2001-11-09", 3L, 6L, "Las Rosas 321", Usuario.Rol.CLIENTE);
    }

    private void crearSiFalta(String run, String nombre, String apellidos, String email, String password,
                              String fechaNacimiento, Long regionId, Long comunaId, String direccion, Usuario.Rol rol) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return;
        }
        usuario(run, nombre, apellidos, email, password, fechaNacimiento, regionId, comunaId, direccion, rol);
    }

    private Region region(String nombre) {
        Region region = new Region();
        region.setNombre(nombre);
        return regionRepository.save(region);
    }

    private void comuna(Region region, String nombre) {
        Comuna comuna = new Comuna();
        comuna.setRegionId(region.getId());
        comuna.setNombre(nombre);
        comunaRepository.save(comuna);
    }

    private void usuario(String run, String nombre, String apellidos, String email, String password,
                         String fechaNacimiento, Long regionId, Long comunaId, String direccion, Usuario.Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setEmail(email);
        // Las claves de prueba tambien quedan encriptadas con BCrypt
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        usuario.setRegionId(regionId);
        usuario.setComunaId(comunaId);
        usuario.setDireccion(direccion);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }
}
