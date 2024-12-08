package pl.vibank.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import pl.vibank.security.filter.CsrfFilter;
import pl.vibank.security.filter.JwtFilter;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProjectSecurityConfig {

    private final JwtFilter jwtFilter;
    private final CsrfFilter csrfFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            };
            c.configurationSource(source);
        });

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.csrf(
                c -> c.disable()
        );

        http.authorizeHttpRequests(
                (request) -> {
                    request.requestMatchers("/auth/create-user").hasRole("ADMIN");//1.
                    request.requestMatchers("/auth/restart-tries").hasRole("ADMIN");//2.
                    request.requestMatchers("/auth/**","/photos/**").permitAll();//3.
                    request.requestMatchers("/transactions/**").authenticated();//4.
                    request.requestMatchers(HttpMethod.GET, "/accounts/**").authenticated();//5.
                    request.requestMatchers(HttpMethod.POST, "/accounts/**").hasRole("ADMIN")//6.
                            .anyRequest().denyAll();//7.
                }
        );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(csrfFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
