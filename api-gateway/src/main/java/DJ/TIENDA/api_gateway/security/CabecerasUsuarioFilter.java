package DJ.TIENDA.api_gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// Despues de validar el token, el gateway le pasa al microservicio el id y el rol
// del usuario en cabeceras. El cliente no puede falsificarlas porque aca se borran
// las que vengan desde afuera y se vuelven a escribir con los datos del token.
@Component
public class CabecerasUsuarioFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> chain.filter(conCabeceras(exchange, auth)))
                .switchIfEmpty(Mono.defer(() -> chain.filter(conCabeceras(exchange, null))));
    }

    private ServerWebExchange conCabeceras(ServerWebExchange exchange, Authentication auth) {
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        builder.headers(headers -> {
            headers.remove("X-Usuario-Id");
            headers.remove("X-Usuario-Rol");
            headers.remove("X-Usuario-Email");
        });

        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String id = jwt.getClaimAsString("id");
            String rol = jwt.getClaimAsString("rol");
            if (id != null) builder.header("X-Usuario-Id", id);
            if (rol != null) builder.header("X-Usuario-Rol", rol);
            builder.header("X-Usuario-Email", jwt.getSubject());
        }
        return exchange.mutate().request(builder.build()).build();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
