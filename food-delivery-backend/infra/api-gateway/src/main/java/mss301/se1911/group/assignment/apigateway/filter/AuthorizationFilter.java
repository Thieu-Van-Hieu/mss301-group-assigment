package mss301.se1911.group.assignment.apigateway.filter;

import mss301.se1911.group.assignment.commonconstants.utils.GatewayConstraints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers((headers) -> {
                    headers.set(GatewayConstraints.HEADER_USER_ID, "dummy-user-id");
//                    headers.set(GatewayConstraints.HEADER_AUTHORIZATION, "Bearer dummy-token");
                    headers.set(GatewayConstraints.HEADER_ROLE, "dummy-role");
                })
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return GatewayConstraints.ORDER_AUTHORIZATION_ID;
    }
}
