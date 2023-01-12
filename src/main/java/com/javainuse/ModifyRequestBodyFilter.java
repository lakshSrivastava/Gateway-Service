package com.javainuse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ModifyRequestBodyFilter implements GlobalFilter, Ordered {

    final Logger logger = LoggerFactory.getLogger(ModifyRequestBodyFilter.class);

    @Autowired
    private ModifyRequestBodyGatewayFilterFactory modifyRequestBodyGWFilter;
    @Autowired
    private RequestBodyRewrite requestBodyRewrite;

    public ModifyRequestBodyFilter() {
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Applying encrypt-decrypt filter3");
        return modifyRequestBodyGWFilter
                .apply(
                        new ModifyRequestBodyGatewayFilterFactory.Config()
                                .setRewriteFunction(String.class, String.class, requestBodyRewrite))
                .filter(exchange, chain);
    }
    @Override
    public int getOrder() {
        return 1;
    }
}
