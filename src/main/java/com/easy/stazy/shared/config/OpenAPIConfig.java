package com.easy.stazy.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfiguration class provides the OpenAPI configuration for the HRMS application.
 * It defines the metadata for the API documentation and sets up security requirements for endpoints.
 * This configuration is used to generate the Swagger UI documentation for API consumers.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configures the OpenAPI specification for the HRMS application.
     * This method sets the API title, description, version, license, and external documentation.
     * It also configures security schemes for JWT Bearer token authentication.
     *
     * @return OpenAPI instance with the configuration settings.
     */
    @Bean
    public OpenAPI configureOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title("StazyApplication")
                        .description("Stayzy")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("stazy App Documentation")
                        .url("https://springboot.wiki.github.org/docs"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName,
                        new SecurityScheme().name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}