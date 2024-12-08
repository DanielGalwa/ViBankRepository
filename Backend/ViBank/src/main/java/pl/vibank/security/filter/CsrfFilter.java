package pl.vibank.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.vibank.security.service.CookieService;
import pl.vibank.security.service.JWTsService;

import java.io.IOException;
import java.util.Optional;

@Component
public class CsrfFilter extends OncePerRequestFilter {

    private final String JWT_COOKIE_NAME;
    private final String CSRF_COOKIE_NAME;
    private final CookieService cookieService;
    private final JWTsService jwtsService;

    public CsrfFilter(@Value("${jwt.cookie-name}")String JWT_COOKIE_NAME,
                      @Value("${csrf.cookie-name}") String CSRF_COOKIE_NAME,
                      CookieService cookieService,
                      JWTsService jwtsService) {

        this.JWT_COOKIE_NAME = JWT_COOKIE_NAME;
        this.CSRF_COOKIE_NAME = CSRF_COOKIE_NAME;
        this.cookieService = cookieService;
        this.jwtsService = jwtsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String method = request.getMethod();
        if (method.equals("GET") || method.equals("HEAD") || method.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        String csrfTokenFromHeader = request.getHeader("X-CSRF-Token");
        if(csrfTokenFromHeader != null) {
            Optional<Cookie> jwtCookie = cookieService.getCookie(request,JWT_COOKIE_NAME);
            Optional<Cookie> csrfCookie = cookieService.getCookie(request,CSRF_COOKIE_NAME);
            if(jwtCookie.isPresent() &&  csrfCookie.isPresent()) {
                String jwtToken = jwtCookie.get().getValue();
                Claims claimsFromToken = jwtsService.getClaimsFromToken(jwtToken);
                String csrf = claimsFromToken.get("csrf", String.class);
                if(csrf != null && csrf.equals(csrfTokenFromHeader)) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Brak lub zły token CSRF");
    }
}
