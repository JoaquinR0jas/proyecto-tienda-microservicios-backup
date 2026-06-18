package DJ.TIENDA.ms_pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsPedidosApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsPedidosApplication.class, args);
    }

    @Bean
    public OpenAPI tiendaOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("http://localhost:8080").description("API Gateway"));
    }
}
