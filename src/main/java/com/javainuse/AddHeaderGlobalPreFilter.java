package com.javainuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AddHeaderGlobalPreFilter implements GlobalFilter, Ordered {
    final Logger logger = LoggerFactory.getLogger(AddHeaderGlobalPreFilter.class);

    private List<HttpMessageReader<?>> getMessageReaders() {
        return HandlerStrategies.withDefaults().messageReaders();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Global Pre Filter executed");
        String profile_id = exchange.getRequest().getHeaders().getFirst("PROFILE_ID");
        String token;
        if(null == profile_id) {
            token = "Bearer Token Generated at GW";
        } else {
            token = "Bearer Token Fetched From Profile svc at GW";
        }
        // adding auth token from gateway service
        ServerWebExchange updatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate().
                        header(HttpHeaders.AUTHORIZATION, token)
                        .build())
                .build();
        return chain.filter(updatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    } // low order ->  high priority
}