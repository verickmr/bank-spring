package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Contas Bancárias")
                        .description("API REST para gerenciamento de correntistas, contas e operações financeiras.")
                        .version("1.0.0"));
    }
}