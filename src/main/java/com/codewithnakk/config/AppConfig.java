package com.codewithnakk.config;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).authorizeHttpRequests(Authorize -> Authorize.requestMatchers("/api/**").authenticated().anyRequest().permitAll())
                .addFilterBefore(new JwtValidator() , BasicAuthenticationFilter.class).csrf(AbstractHttpConfigurer::disable).cors(cors -> {
                    try {
                        cors.configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration cfg = new CorsConfiguration();
                                cfg.setAllowedOrigins(Arrays.asList("http://localhost:3002"));
                                cfg.setAllowedMethods(Collections.singletonList("*"));
                                cfg.setAllowCredentials(true);
                                cfg.setAllowedHeaders(Collections.singletonList("*"));
                                cfg.setExposedHeaders(Arrays.asList("Authorization"));
                                cfg.setMaxAge(3600L);
                                return cfg;
                            }
                        }).and().httpBasic().and().formLogin();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
