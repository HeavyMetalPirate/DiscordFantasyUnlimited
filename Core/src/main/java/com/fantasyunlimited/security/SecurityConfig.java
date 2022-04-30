package com.fantasyunlimited.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.csrf().disable();

        http
            .csrf()
                .ignoringAntMatchers("/api/user/register", "/login", "/perform_login")
            .and()
            .authorizeRequests()
                .antMatchers("/", "/content/**").permitAll()
                .antMatchers("/api/content/**").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/game/**").hasAnyRole("USER", "ADMIN", "GAMEMASTER")
                .antMatchers("/api/user/register", "/login*", "/perform_login*", "/logout").permitAll()
                .antMatchers("/api/user/current").authenticated()
            .and()
                .formLogin()
                .usernameParameter("alias")
                .passwordParameter("password")
                .loginPage("/login")
                .failureUrl("/login?error")
                .defaultSuccessUrl("/game", true)
                .permitAll()
            .and()
                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/")
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, PasswordEncoder passwordEncoder) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
