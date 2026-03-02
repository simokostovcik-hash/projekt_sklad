package cz.projekt_sklad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Šifrování hesel
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/css/**").permitAll() // Veřejné stránky
                        .anyRequest().authenticated() // Vše ostatní vyžaduje login
                )
                .formLogin(login -> login
                        .defaultSuccessUrl("/kava/vse", true) // Kam jít po přihlášení
                        .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/")); // Kam jít po odhlášení

        return http.build();
    }
}