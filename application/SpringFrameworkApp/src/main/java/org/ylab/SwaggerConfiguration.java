package org.ylab;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.ylab.aspects.LoggableAspect;
import org.ylab.meter.MeterService;
import org.ylab.reading.ReadingService;
import org.ylab.user.UserService;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;


@Configuration
@EnableOpenApi
@EnableAspectJAutoProxy
public class SwaggerConfiguration {
    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor validationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public LoggableAspect getLoggableAspect() {
        return new LoggableAspect();
    }

    @Bean
    public AnnotationAwareAspectJAutoProxyCreator getAnnotationAwareAspectJAutoProxyCreator() {
        return new AnnotationAwareAspectJAutoProxyCreator();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .forCodeGeneration(true);
    }
    private ApiInfo apiInfo() {
        return new ApiInfo("REST APIs", "Technically Correct APIs", "1.0",
                "urn:tos",
                new Contact("Technically Correct R&D",
                        "www.technically-correct.com",
                        "contact@technically-correct.com"),
                "License", "www.technically-correct.com/license",
                Collections.emptyList());
    }

}

