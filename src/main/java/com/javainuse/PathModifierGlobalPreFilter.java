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
public class PathModifierGlobalPreFilter implements GlobalFilter, Ordered {
    final Logger logger = LoggerFactory.getLogger(PathModifierGlobalPreFilter.class);

    private List<HttpMessageReader<?>> getMessageReaders() {
        return HandlerStrategies.withDefaults().messageReaders();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //path update logic localhost:3232/iss/users/{profileId}/* -> localhost:3232/iss/*
        logger.info("Global Pre Filter executed");
        String profileId = null;
        String path = exchange.getRequest().getPath().value();
        String[] arr = path.split("/");
        if(arr.length >= 4 && arr[2].equals("users")) {
            profileId = arr[3];
        }
        if(profileId != null) {
            String newPath = "";
            for(int i=1 ; i< arr.length; i++) {
                if(i==2 || i==3) {
                    continue;
                }
                newPath = newPath + "/" + arr[i];
            }
            ServerHttpRequest httpRequest = exchange.getRequest().mutate().path(newPath).build();
            ServerWebExchange updatedExchange = exchange.mutate()
                    .request(httpRequest)
                    .build();
            return chain.filter(updatedExchange);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    } // low order ->  high priority
}