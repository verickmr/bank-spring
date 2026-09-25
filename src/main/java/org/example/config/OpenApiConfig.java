package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI bankOpenApi() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .info(new Info()
                        .title("API de Contas Bancárias")
                        .description("API REST para gerenciamento de correntistas, contas e operações financeiras.")
                        .version("1.0.0"));
    }
}
