package DJ.TIENDA.api_gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

// Seguridad centralizada: el gateway revisa el token JWT antes de dejar pasar
// la peticion a cualquier microservicio. Asi no hay que repetir la misma
// configuracion en los seis servicios.
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // Tiene que ser la misma clave con la que firma auth-service
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            // El CORS lo maneja spring.cloud.gateway.globalcors
            .cors(ServerHttpSecurity.CorsSpec::disable)
            .authorizeExchange(ex -> ex
                // El navegador manda un OPTIONS antes de cada peticion
                .pathMatchers(HttpMethod.OPTIONS).permitAll()

                // Login, refresh y validacion son publicos
                .pathMatchers("/api/v1/auth/**").permitAll()
                // Documentacion: la propia y la de cada servicio (Swagger agregado)
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                        "/api/v1/*/v3/api-docs/**", "/webjars/**").permitAll()

                // Los mensajes de contacto traen nombres y correos: leerlos o borrarlos
                // es solo del ADMIN. Va antes del GET publico de contenido.
                .pathMatchers(HttpMethod.POST, "/api/v1/contenido/mensajes").permitAll()
                .pathMatchers("/api/v1/contenido/mensajes/**").hasRole("ADMIN")

                // La tienda se puede ver sin iniciar sesion
                .pathMatchers(HttpMethod.GET, "/api/v1/catalogo/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/contenido/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/regiones/**",
                        "/api/v1/usuarios/comunas/**").permitAll()

                // El registro de clientes es publico (lo usa la pagina de registro)
                .pathMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()

                // Cualquier usuario con sesion puede ver SU perfil (lo usa el checkout
                // para rellenar el formulario); ms-usuarios revisa que el id sea el suyo.
                // {id:\\d+} acepta solo numeros, asi /auth y /buscar no entran aca.
                .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/{id:\\d+}").authenticated()

                // El resto de usuarios solo lo administra el ADMIN
                .pathMatchers("/api/v1/usuarios/**").hasRole("ADMIN")

                // Un cliente ve sus propias compras; admin y vendedor ven todas
                .pathMatchers(HttpMethod.GET, "/api/v1/pedidos/usuario/**")
                        .hasAnyRole("ADMIN", "CLIENTE")
                // Detalle de un pedido: el cliente solo el suyo (lo revisa ms-pedidos)
                .pathMatchers(HttpMethod.GET, "/api/v1/pedidos/{id:\\d+}")
                        .hasAnyRole("ADMIN", "VENDEDOR", "CLIENTE")
                .pathMatchers(HttpMethod.GET, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMIN", "VENDEDOR")
                .pathMatchers(HttpMethod.POST, "/api/v1/pedidos/**")
                        .hasAnyRole("ADMIN", "CLIENTE")
                .pathMatchers("/api/v1/pedidos/**").hasRole("ADMIN")

                // Los pagos los hace el cliente al comprar
                .pathMatchers("/api/v1/pagos/**").hasAnyRole("ADMIN", "CLIENTE")

                // Productos y categorias los modifica solo el ADMIN,
                // el vendedor nada mas los mira (GET permitido arriba)
                .pathMatchers("/api/v1/catalogo/**").hasRole("ADMIN")

                // Mensajes y articulos: escribir es solo del ADMIN
                .pathMatchers("/api/v1/contenido/**").hasRole("ADMIN")

                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    // Verifica la firma y la fecha de expiracion del token.
    // auth-service firma con HS384 porque la clave secreta tiene 56 bytes
    // (jjwt elige HS384 sola para ese largo). Tiene que ser el mismo algoritmo aca.
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKey clave = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

    // Convierte el claim "rol" del token en ROLE_ADMIN, ROLE_VENDEDOR o ROLE_CLIENTE
    private Converter<org.springframework.security.oauth2.jwt.Jwt, Mono<AbstractAuthenticationToken>>
    jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            if (rol == null) rol = "CLIENTE";
            return List.of(new SimpleGrantedAuthority("ROLE_" + rol));
        });
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
}
