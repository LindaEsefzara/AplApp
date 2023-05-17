package com.Linda.AplApp.Configuration;

import com.Linda.AplApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private DataSource dataSource;

    @Autowired
    public SecurityConfiguration(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, DataSource dataSource){
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.userService=userService;
        this.dataSource=dataSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(requests ->{
                        requests
                                .requestMatchers( "/","/login","/logout" , "/error", "/rest/**", "/register", "/static/**").permitAll()
                            .requestMatchers("/student/**").hasRole("STUDENT")
                            .requestMatchers("/teacher/**").hasRole("TEACHER")
                                .anyRequest()
                                .authenticated();

                        }
                )

                .formLogin( formlogin ->{
                            formlogin.loginPage("/login").failureUrl("/login?e=true")
                                    .defaultSuccessUrl("/login/success", true).usernameParameter("email").passwordParameter("password");
                        }
                )

                .rememberMe(rememberMe -> {
                            rememberMe
                                    .rememberMeParameter("remember-me")
                                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
                                    .key("someSecureKey")
                                    .userDetailsService((UserDetailsService) userService);
                                    }
                )
                .logout(logout -> {
                    try {
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/").and().exceptionHandling()
                                .accessDeniedPage("/login?d=true");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
                )
                .authenticationProvider(authenticationOverride());
        return http.build();
    }

    public AuthenticationProvider authenticationOverride() {
        AuthenticationProvider provider = new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                return null;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return false;
            }
        };

        return provider;
    }


}
