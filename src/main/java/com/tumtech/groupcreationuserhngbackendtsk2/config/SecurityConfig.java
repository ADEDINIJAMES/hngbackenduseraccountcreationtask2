package com.tumtech.groupcreationuserhngbackendtsk2.config;

import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import com.tumtech.groupcreationuserhngbackendtsk2.util.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private UserServiceImplementation authServiceImplementation;

    public SecurityConfig (JwtAuthenticationFilter jwtAuthenticationFilter, @Lazy UserServiceImplementation authServiceImplementation){
        this.jwtAuthenticationFilter =jwtAuthenticationFilter;
        this.authServiceImplementation =authServiceImplementation;
    }
    @Bean
    public PasswordEncoder passwordEncoder (){return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider (){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(username -> authServiceImplementation.loadUserByUsername(username));
        return daoAuthenticationProvider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(httpRequests -> httpRequests
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/users/**","/api/organisations/**").authenticated())
                .logout(logout-> logout.deleteCookies("remove").invalidateHttpSession(true)

                ).
                sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
