package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global CORS configuration to allow the React frontend to call the backend.
 *
 * PUBLIC_INTERFACE
 */
@Configuration
public class CorsConfig {

    @Value("${REACT_APP_FRONTEND_URL:}")
    private String frontendUrlEnv;

    // PUBLIC_INTERFACE
    /**
     * Create a CORS filter to allow requests from the frontend origin.
     * Falls back to http://localhost:3000 if env not set.
     *
     * @return CorsFilter bean
     */
    @Bean
    @ConditionalOnMissingBean(CorsFilter.class)
    public CorsFilter corsFilter() {
        String origin = (frontendUrlEnv == null || frontendUrlEnv.isBlank())
                ? "http://localhost:3000"
                : frontendUrlEnv;

        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern(origin);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
