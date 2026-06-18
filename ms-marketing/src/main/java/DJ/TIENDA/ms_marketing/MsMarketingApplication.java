package DJ.TIENDA.ms_marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MsMarketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsMarketingApplication.class, args);
    }

    @Bean
    public OpenAPI tiendaOpenAPI() {
        return new OpenAPI()
            .addServersItem(new Server().url("http://localhost:8080").description("API Gateway"));
    }
}