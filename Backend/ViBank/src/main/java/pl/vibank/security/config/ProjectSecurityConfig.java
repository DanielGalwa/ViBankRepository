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
                //Nagłówek: Access-Control-Allow-Origin
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                //Nagłówek: Access-Control-Allow-Methods
                config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
                //Nagłówek: Access-Control-Allow-Headers
                config.setAllowedHeaders(List.of("*"));
                //Nagłówek: Access-Control-Allow-Credentials
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
        //Filtr uwierzytelniania
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        //Filtr zabezpieczeń przeciwko CSRF
        http.addFilterAfter(csrfFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

//config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

// request.requestMatchers("/test/**").authenticated();
//https://stackoverflow.com/questions/59302026/spring-security-why-adding-the-jwt-filter-before-usernamepasswordauthenticatio