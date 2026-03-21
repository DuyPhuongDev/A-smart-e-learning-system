package com.hcmut.lms.coachingchatbot.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "qdrant")
public class QdrantConfig {

    private String host = "localhost";
    private int port = 6334;
    private String apiKey;
    private boolean useTls = false;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, useTls);

        if (apiKey != null && !apiKey.isBlank()) {
            builder.withApiKey(apiKey);
        }

        return new QdrantClient(builder.build());
    }
}
