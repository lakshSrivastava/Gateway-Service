package com.javainuse.fillters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
        //path update logic localhost:3232/iss/users/{profileId}/* -> localhost:3232/iss/users/personId
        logger.info("path update Pre Filter executed");
        String profileId = null;
        String path = exchange.getRequest().getPath().value();
        String[] arr = path.split("/");
        if(arr.length >= 4 && arr[2].equals("users")) {
            profileId = arr[3];
        }
        if(profileId != null) {
            String personId = getPersonIdFromProfile(profileId); // exception
            arr[3] = personId;
            String newPath = "";
            for(int j = 1; j < arr.length;j++) {
                newPath = newPath + "/" +  arr[j];
            }
            ServerHttpRequest httpRequest = exchange.getRequest().mutate().path(newPath).build();
            ServerWebExchange updatedExchange = exchange.mutate()
                    .request(httpRequest)
                    .build();
            return chain.filter(updatedExchange);
        }
        return chain.filter(exchange);
    }

    private String getPersonIdFromProfile(String profileId) {
        final String uri = "http://localhost:8081/personId/"+profileId;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        if(true) {
            throw new NullPointerException("Object is null");
        }
        return result;
    }

    @Override
    public int getOrder() {
        return 0;
    } // low order ->  high priority
}