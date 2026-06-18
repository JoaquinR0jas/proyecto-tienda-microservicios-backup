package DJ.TIENDA.ms_catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class MsCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCatalogoApplication.class, args);
	}

	@Bean
	public OpenAPI tiendaOpenAPI() {
		return new OpenAPI()
			.addServersItem(new Server().url("http://localhost:8080").description("API Gateway"));
	}
}
