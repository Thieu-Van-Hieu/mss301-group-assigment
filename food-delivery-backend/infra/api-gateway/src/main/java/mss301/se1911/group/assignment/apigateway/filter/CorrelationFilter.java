package mss301.se1911.group.assignment.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonconstants.utils.GatewayConstraints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CorrelationFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String correlationId = request.getHeaders().getFirst(GatewayConstraints.HEADER_CORRELATION_ID);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = java.util.UUID.randomUUID().toString();
        }

        log.info("Correlation ID: {}", correlationId);
        ServerHttpRequest modifiedRequest = request.mutate()
                .header(GatewayConstraints.HEADER_CORRELATION_ID, correlationId)
                .build();

        final String finalCorrelationId = correlationId;

        exchange.getResponse().beforeCommit(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().set(GatewayConstraints.HEADER_CORRELATION_ID, finalCorrelationId);
            return Mono.empty();
        });

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    log.info("Completed request with Correlation ID: {}", finalCorrelationId);
                }));
    }

    @Override
    public int getOrder() {
        return GatewayConstraints.ORDER_CORRELATION_ID;
    }
}
