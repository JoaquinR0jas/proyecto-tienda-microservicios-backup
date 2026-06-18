package DJ.TIENDA.ms_usuarios;
// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.
// y tambien el security service, ya que dimos un rol y el profe pidio eso, ya tenemos gestion de roles

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
@EnableDiscoveryClient
@SpringBootApplication
public class MsUsuariosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsUsuariosApplication.class, args);
	}

	@Bean
	public OpenAPI tiendaOpenAPI() {
		return new OpenAPI()
			.addServersItem(new Server().url("http://localhost:8080").description("API Gateway"));
	}
}
