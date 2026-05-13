package com.hcmut.lms.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
@EnableAsync
public class AwsConfig {

  private static final String awsRegion = "ap-southeast-1";

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.of(awsRegion))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.of(awsRegion))
        .build();
  }

  @Bean
  public SqsClient sqsClient() {
    return SqsClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.of(awsRegion))
        .build();
  }
}
