package com.app.Config;

import com.app.Exceptions.CustomAccessDeniedHandler;
import com.app.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisSessionRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CustomAccessDeniedHandler accessDeniedHandler;

    @Primary
    public SessionRepository sessionRepository(RedisTemplate<String,Object> redisTemplate) {
        return new RedisSessionRepository(redisTemplate);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    public DefaultWebSecurityExpressionHandler customWebSecurityExpressionHandler(){
        DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((authorize) ->
                authorize.requestMatchers(
                    HttpMethod.GET,"/api/**").permitAll()
                    .requestMatchers("/api/user/info").authenticated()
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST,"/api/author/create").hasAnyRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE,"/api/author/delete/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,"api/comment/create").authenticated()
                    .requestMatchers(HttpMethod.DELETE,"api/comment/delete/**").authenticated()
                    .requestMatchers(HttpMethod.POST,"/api/ebook/upload").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,"/api/ebook/download/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE,"/api/ebook/delete/**").authenticated()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .sessionManagement(session ->
                session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .expiredUrl("/login?invalid-session=true")
            );
        return http.build();
    }

}
