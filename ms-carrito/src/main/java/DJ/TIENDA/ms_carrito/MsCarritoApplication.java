package DJ.TIENDA.ms_carrito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient // Se registra en Eureka
@EnableFeignClients    // Habilita Feign para consultar ms-catalogo y ms-inventario
public class MsCarritoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsCarritoApplication.class, args);
    }

    @Bean
    public OpenAPI tiendaOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("http://localhost:8080").description("API Gateway"));
    }
}