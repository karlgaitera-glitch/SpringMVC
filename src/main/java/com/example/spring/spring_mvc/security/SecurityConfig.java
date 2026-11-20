package com.example.spring.spring_mvc.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.spring.spring_mvc.service.CustomUserDetailsService;

/**
 * Spring Security configuration class defining HTTP security rules,
 * password encoding, and authentication management beans.
 */

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.setUserDetailsService(userDetailsService);
    }

    /**
     * Configures the security filter chain for incoming HTTP requests.
     * Enables CORS, disables CSRF protection, sets session management to
     * {@link SessionCreationPolicy#IF_REQUIRED}, permits unauthenticated
     * access to {@code /api/auth/**} and {@code OPTIONS} preflight requests,
     * restricts {@code /api/students/**} to {@code USER} or {@code ADMIN}
     * authorities, requires authentication for all other endpoints, and
     * enables HTTP Basic authentication.
     *
     * @param security the {@link HttpSecurity} builder to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity security)
            throws Exception {
        return security
                .cors(cors -> cors.configure(security))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                    session -> session
                        .sessionCreationPolicy(
                            SessionCreationPolicy.IF_REQUIRED)
                    )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/students/**").hasAnyAuthority(
                            "USER", "ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * Provides a BCrypt password encoder bean for hashing passwords.
     *
     * @return a {@link BCryptPasswordEncoder} instance
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean from the Spring Security
     * configuration.
     *
     * @param authentication the {@link AuthenticationConfiguration} 
     * to retrieve the manager from
     * @return the configured {@link AuthenticationManager}
     * @throws Exception if the authentication manager cannot be retrieved
     */
    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authentication) throws Exception {
        return authentication.getAuthenticationManager();
    }

    /**
     * Retrieves the configured custom user details service.
     *
     * @return the {@link CustomUserDetailsService} instance
     */
    public CustomUserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    /**
     * Sets the custom user details service.
     *
     * @param userDetailsService the {@link CustomUserDetailsService} to set
     */
    public void setUserDetailsService(
            CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) rules for incoming
     * HTTP requests across all endpoints ({@code /**}).
     * Allows credentials, standard REST HTTP methods, specific request
     * headers, and requests originating from local development hosts.
     *
     * @return the configured {@link CorsConfigurationSource} instance
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5500",
                                            "http://127.0.0.1:5500",
                                            "http://192.168.137.204:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST",
                                            "PUT", "PATCH",
                                            "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization",
                                            "Cache-Control",
                                            "Content-Type"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}