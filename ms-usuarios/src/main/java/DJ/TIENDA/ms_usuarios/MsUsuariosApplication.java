package DJ.TIENDA.ms_usuarios;
// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@EnableDiscoveryClient
@SpringBootApplication
public class MsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

}
