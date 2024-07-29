package com.example.healthylife.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.accessKey}")
    private String  accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.region.static}")
    private String region;


//    //에러 코드 정의
//    public static final String EMPTY_FILE_EXCEPTION = "EMPTY_FILE_EXCEPTION";
//
//    // 사용자 정의 예외 클래스
//    public static class S3Exception extends RuntimeException {
//        private final String errorCode;
//
//        public S3Exception(String errorCode) {
//            super("S3 Error: " + errorCode);
//            this.errorCode = errorCode;
//        }
//
//        public String getErrorCode() {
//            return errorCode;
//        }

    @Bean
    public AmazonS3 amazonS3(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,secretKey);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

}
