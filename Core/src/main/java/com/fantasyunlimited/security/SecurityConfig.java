package com.fantasyunlimited.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://localhost:4200","http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
            .cors()
                .and()
            .csrf()
                .ignoringRequestMatchers("/api/user/register", "/login", "/perform_login")
                .ignoringRequestMatchers("/gs-guide-websocket/**", "/api/websocket/**")
            .and()
            .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry).and()
            .and()
                .authorizeHttpRequests()
                    .requestMatchers("/gs-guide-websocket/**", "/api/websocket/**").permitAll()
                    .requestMatchers("/", "/content/**").permitAll()
                    .requestMatchers("/api/content/**").permitAll()
                    .requestMatchers("/register").permitAll()
                    .requestMatchers("/api/", "/api/swagger-ui/**", "/v3/**").permitAll()
                    .requestMatchers("/game/**").hasAnyRole("USER", "ADMIN", "GAMEMASTER")
                    .requestMatchers("/api/user/register", "/login*", "/perform_login*", "/logout").permitAll()
                    .requestMatchers("/api/user/current").authenticated()
                    //.anyRequest().permitAll()
                /*.and()
                    .formLogin()
                    .usernameParameter("alias")
                    .passwordParameter("password")
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    .defaultSuccessUrl("/game", true)
                    .successHandler(authenticationSuccessHandler)
                    .permitAll()

                .and()
                    .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/")
                    .permitAll()
                    */
            .and()
                .build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }
}
