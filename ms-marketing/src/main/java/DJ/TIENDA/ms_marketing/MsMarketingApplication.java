package DJ.TIENDA.ms_marketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsMarketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsMarketingApplication.class, args);
    }
}