package mss301.se1911.group.assignment.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import mss301.se1911.group.assignment.commonconstants.utils.GatewayConstraints;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(GatewayConstraints.HEADER_CORRELATION_ID);
        String clientId = exchange.getRequest().getHeaders().getFirst(GatewayConstraints.HEADER_USER_ID);

        log.info("Incoming request: Correlation ID = {}, Client ID = {}, Path = {}",
                header, clientId, exchange.getRequest().getURI().getRawPath());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Log khi trả về
        }));
    }

    @Override
    public int getOrder() {
        return GatewayConstraints.ORDER_LOGGING_ID;
    }
}
