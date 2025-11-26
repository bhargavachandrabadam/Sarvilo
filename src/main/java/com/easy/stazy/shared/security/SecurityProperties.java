package com.easy.stazy.shared.security;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private List<String> publicPaths;
    private List<String> unauthorizedApiPaths;
    private List<String> adminApiPaths;
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private List<String> exposedHeaders;
    private boolean allowCredentials;
    private long maxAge;
    private Cors cors = new Cors();

    @Data
    public static class Cors {
        private boolean enabled = true;
        private String pathPattern = "/**";

        public String getPathPattern() {
            return (pathPattern == null || pathPattern.trim().isEmpty()) ? "/**" : pathPattern;
        }

        public void setPathPattern(String pathPattern) {
            this.pathPattern = (pathPattern == null || pathPattern.trim().isEmpty()) ? "/**" : pathPattern;
        }
    }

    @PostConstruct()
    public void vd(){
        System.out.println("public publicPaths "+getPublicPaths());
    }

}
