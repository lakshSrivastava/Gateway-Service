package com.javainuse;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequestBodyRewrite implements RewriteFunction<String, String> {
    final Logger logger = LoggerFactory.getLogger(RequestBodyRewrite.class);

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        Gson gson = new Gson();
        try {
            Map<String, Object> requestBodyMap = gson.fromJson(body, Map.class);
            if (requestBodyMap.containsKey("encryptedDataList")) {
                List<Map<String, Object>> decryptedDataList = (List<Map<String, Object>>) requestBodyMap.remove("encryptedDataList");
                decryptedDataList.stream().map(map -> transformAndDecrypt(map))
                        .collect(Collectors.toList());
                requestBodyMap.put("decryptedDataList", decryptedDataList);
            }
            return Mono.just(gson.toJson(requestBodyMap, Map.class));
        } catch (Exception ex) {
            logger.error(
                    "An error occured while transforming the request body in class RequestBodyRewrite. {}",
                    ex);
            // Throw custom exception here
            throw new RuntimeException(
                    "An error occurred while transforming the request body in class RequestBodyRewrite.");
        }
    }

    private Map<String, Object> transformAndDecrypt(Map<String, Object> map) {
        if (map.containsKey("content")) {
            String contentVal = (String) map.get("content");
            String decryptedContentVal = EncryptDecryptHelper.doDecryption(contentVal);
            map.put("content", decryptedContentVal);
        }
        return map;
    }


}
