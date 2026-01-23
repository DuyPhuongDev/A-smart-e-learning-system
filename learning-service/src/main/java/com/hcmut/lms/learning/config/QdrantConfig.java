package com.hcmut.lms.learning.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data // Để Spring dùng setter nạp dữ liệu từ application.yml vào biến
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "qdrant")
public class QdrantConfig {

    // 1. Khai báo các biến cấu hình ngay tại đây
    private String host = "localhost";
    private int port = 6334;
    private String apiKey;
    private boolean useTls = false;

    // 2. Tạo Bean Client
    @Bean
    public QdrantClient qdrantClient() {
        log.info("Connecting to Qdrant at {}:{}", host, port);

        // Tạo Builder
        QdrantGrpcClient.Builder grpcClientBuilder = QdrantGrpcClient.newBuilder(
                host,
                port,
                useTls
        );

        // Nạp API Key nếu có
        if (apiKey != null && !apiKey.isBlank()) {
            grpcClientBuilder.withApiKey(apiKey);
        }

        // Build Client
        QdrantClient client = new QdrantClient(grpcClientBuilder.build());

        // Test kết nối (Tùy chọn)
        try {
            client.listCollectionsAsync().get();
            log.info("Successfully connected to Qdrant!");
        } catch (Exception e) {
            log.error("Failed to connect to Qdrant: {}", e.getMessage());
        }

        return client;
    }
}
