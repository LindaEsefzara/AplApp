package com.Linda.AplApp.Configuration;

import com.Linda.AplApp.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/login", "/logout", "/error", "/rest/**", "/register", "/static/**").permitAll()
                .antMatchers("/student/**").hasRole("STUDENT")
                .antMatchers("/teacher/**").hasRole("TEACHER")
                .anyRequest().authenticated()
                .and()
                .formLogin(formlogin ->
                        formlogin
                                .loginPage("/login")
                                .failureUrl("/login?e=true")
                                .defaultSuccessUrl("/login/success", true)
                                .usernameParameter("email")
                                .passwordParameter("password")
                )
                .rememberMe(rememberMe ->
                        rememberMe
                                .rememberMeParameter("remember-me")
                                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                                .key("someSecureKey")
                                .userDetailsService((UserDetailsService) userService)
                )
                .logout(logout ->
                        {
                            try {
                                logout
                                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                        .logoutSuccessUrl("/")
                                        .and()
                                        .exceptionHandling()
                                        .accessDeniedPage("/login?d=true");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .authenticationProvider(authenticationProvider());
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return provider;
    }

}
