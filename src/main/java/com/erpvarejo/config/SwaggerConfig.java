package com.erpvarejo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ERP Varejo API")
                        .description("API de validacao fiscal para ERP de varejo brasileiro")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe ERP Varejo")
                                .email("contato@erpvarejo.com")));
    }
}
