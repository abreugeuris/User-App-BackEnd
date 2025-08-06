
package com.backend.usersapp.config;


import com.backend.usersapp.auth.filters.JwtAuthenticationFilter;
import com.backend.usersapp.auth.filters.JwtValidationFilter;
import com.backend.usersapp.services.JpaUserDetailsService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class SecurityConfig {

    private static final String ADMIN_USER = "ADMIN";
    private static final String COSTUMER_USER = "USER";


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(JpaUserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig,
            DaoAuthenticationProvider provider) throws Exception {

        // Registramos el provider manualmente
        return new ProviderManager(provider);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.GET, "/users", "/users/api/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole(ADMIN_USER, COSTUMER_USER)
                                .requestMatchers(HttpMethod.POST, "/users").hasAnyRole(ADMIN_USER)
                                .requestMatchers(HttpMethod.GET, "/users/available").hasAnyRole(ADMIN_USER)
//                        .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasAnyRole("ADMIN")
                                .requestMatchers("/users/**").hasAnyRole(ADMIN_USER)
                                .anyRequest().authenticated()
                )
                .addFilter(new JwtAuthenticationFilter(authManager))
                .addFilter(new JwtValidationFilter(authManager))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = new ArrayList<>();
        Stream<HttpMethod> httpMethodStream = Arrays.stream(HttpMethod.values());
        List<String> methods = httpMethodStream.map(HttpMethod::name).toList();

        origins.add("http://localhost:5173");
//        origins.add("http://localhost:5173/users");

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(methods);
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
            FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(
                    new CorsFilter(corsConfigurationSource()));

            filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
            return filterRegistrationBean;
    }

}