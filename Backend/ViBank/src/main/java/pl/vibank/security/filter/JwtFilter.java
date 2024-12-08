package pl.vibank.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.vibank.security.model.entity.CustomUserDetails;
import pl.vibank.security.service.CookieService;
import pl.vibank.security.service.JWTsService;
import pl.vibank.security.service.JpaUserDetailsService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Component
public class JwtFilter extends OncePerRequestFilter {

    private final String JWT_COOKIE_NAME;
    private final JWTsService jwtsService;
    private final CookieService cookieService;
    private final JpaUserDetailsService jpaUserDetailsService;


    public JwtFilter(@Value("${jwt.cookie-name}") String jwtCookieName,
                     JWTsService jwtsService,
                     CookieService cookieService,
                     JpaUserDetailsService jpaUserDetailsService) {
        this.JWT_COOKIE_NAME = jwtCookieName;
        this.jwtsService = jwtsService;
        this.cookieService = cookieService;
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            Optional<Cookie> jwtCookie = cookieService.getCookie(request,JWT_COOKIE_NAME);

            if (jwtCookie.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }
            String jwt = jwtCookie.get().getValue();

            Optional<Claims> claimsFormRequest = jwtsService.ifFirstPhaseCompletedReturnClaims(jwt);
            if(claimsFormRequest.isPresent()) {
                Claims claims = claimsFormRequest.get();
                String pid = claims.getSubject();
                String role = jwtsService.getClaimsFromToken(jwt).get("role", String.class);
                CustomUserDetails customUserDetails = jpaUserDetailsService.loadUserByUsername(pid);

                if(claims.get("2fa", Boolean.class)){
                    var authentication =
                            new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of(new SimpleGrantedAuthority("ROLE_"  + role)));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

}
