package com.maxtuzz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Swagger API documentation
 * Author: Max Tuzzolino
 */

@Configuration
@EnableSwagger2
@ComponentScan("social.noodle")
public class SwaggerConfig {
    @Bean
    public Docket allApis() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(or (regex("/auth.*"),
                        regex("/user.*")))
                .build();
    }

    // OAuth mocked for JWT
    @Bean
    public SecurityConfiguration security() {
        return new SecurityConfiguration(
                "",
                "",
                "",
                "Noodle",
                "bearer ",
                ApiKeyVehicle.HEADER,
                "Authorization",
                "," /*scope separator*/);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Generic API Swagger Setup")
                .description("Documentation for APIs")
                .termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/maxtuzz")
                .version("2.0")
                .build();
    }
}
