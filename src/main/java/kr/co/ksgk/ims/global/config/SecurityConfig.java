package kr.co.ksgk.ims.global.config;

import kr.co.ksgk.ims.global.jwt.JwtAuthenticationFilter;
import kr.co.ksgk.ims.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final CorsConfig corsConfig;

    private static final String[] WHITELIST = {
            "/", "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/auth/**"
    };

    private static final String[] GET_WHITELIST = {
            // 인증 필요 없이 허용할 GET 요청 URI가 있다면 여기에 추가
    };

    private static final String[] POST_WHITELIST = {
            // 인증 필요 없이 허용할 POST 요청 URI가 있다면 여기에 추가
    };

    private static final String[] PATCH_WHITELIST = {
            // 인증 필요 없이 허용할 PATCH 요청 URI가 있다면 여기에 추가
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.GET, GET_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.POST, POST_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.PATCH, PATCH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilter(corsConfig.corsFilter())
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}