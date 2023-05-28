package com.Linda.AplApp.Configuration;

import com.Linda.AplApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration extends WebSecurityConfiguration {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserDetailsService customUserDetailsService;
    private final UserService userService;
    private final DataSource dataSource;

    public SecurityConfiguration(BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService customUserDetailsService, UserService userService, DataSource dataSource) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customUserDetailsService = customUserDetailsService;
        this.userService = userService;
        this.dataSource = dataSource;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Bean
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .requestMatchers("/", "/login", "/logout", "/error", "/rest/**", "/register", "/static/**").permitAll()
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/teacher/**").hasRole("TEACHER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?e=true")
                .defaultSuccessUrl("/", true)
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    // Hämta användarrollen från autentiseringen
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if (role.equals("USER")) {
                        response.sendRedirect("/studentHome.html");
                    } else if (role.equals("ADMIN")) {
                        response.sendRedirect("/teacherHome.html");
                    } else {
                        // Omdirigera till standard homePage.html om rollen inte matchar någon av de förväntade rollerna
                        response.sendRedirect("/");
                    }
                })
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                .key("someSecureKey")
                .userDetailsService(userService)
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/login?d=true");
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }
}
