package DJ.TIENDA.ms_inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // Sin esto, Eureka no lo ve y Feign no puede llamarlo
public class MsInventarioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsInventarioApplication.class, args);
    }
}