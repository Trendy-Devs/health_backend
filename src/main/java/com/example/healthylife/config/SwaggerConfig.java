package com.example.healthylife.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
//                .consumes(getConsumeContentTypes())
//                .produces(getProduceContentTypes())
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.healthylife"))
                .paths(PathSelectors.ant("/**"))
                .build();
    }

//    private Set<String> getConsumeContentTypes() {
//        Set<String> consumes = new HashSet<>();
//        consumes.add("application/json;charset=UTF-8");
//        consumes.add("application/x-www-form-urlencoded");
//        return consumes;
//    }
//
//    private Set<String> getProduceContentTypes() {
//        Set<String> produces = new HashSet<>();
//        produces.add("application/json;charset=UTF-8");
//        return produces;
//    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("API")
                .description("[healthylife] REST API")
                .contact(new Contact("[healthylife Swagger]", "https://github/....", "BNG"))
                .version("1.0")
                .build();
    }
}
