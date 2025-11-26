package com.easy.stazy.shared.security;


import com.easy.stazy.authentication.dto.RoleType;
import com.easy.stazy.shared.filters.JwtAuthenticationFilter;
import com.easy.stazy.shared.filters.TraceIdResponseFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final TraceIdResponseFilter traceIdResponseFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityProperties   securityProperties;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> {
                    securityProperties.getPublicPaths()
                            .forEach(path -> auth.requestMatchers(path).permitAll());
                    securityProperties.getUnauthorizedApiPaths()
                            .forEach(path -> auth.requestMatchers(path).permitAll());
                    securityProperties.getAdminApiPaths()
                            .forEach(path -> auth.requestMatchers(path).hasRole(RoleType.ADMIN.name()));
                    auth.requestMatchers("/images/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(traceIdResponseFilter, SecurityContextHolderFilter.class);
        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(securityProperties.getAllowedOrigins());
        configuration.setAllowedHeaders(securityProperties.getAllowedHeaders());
        configuration.setAllowedMethods(securityProperties.getAllowedMethods());
        configuration.setExposedHeaders(securityProperties.getExposedHeaders());
        configuration.setAllowCredentials(securityProperties.isAllowCredentials());
        configuration.setMaxAge(securityProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(securityProperties.getCors().getPathPattern(), configuration);
        return source;
    }
}
