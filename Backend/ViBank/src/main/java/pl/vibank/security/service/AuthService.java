package pl.vibank.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.vibank.model.exception.UserBlockedException;
import pl.vibank.security.model.dto.LoginDTO;
import pl.vibank.security.model.enums.Role;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final String JWT_COOKIE_NAME;
    private final JWTsService jwtsService;
    private final AuthenticationManager authenticationManager;
    private final JpaUserDetailsService userDetailsService;
    private final CookieService cookieService;
    private final CodeService codeService;
    private final JpaUserDetailsService jpaUserDetailsService;

    public AuthService(@Value("${jwt.cookie-name}") String jwtCookieName,
                       JWTsService jwtsService,
                       AuthenticationManager authenticationManager,
                       JpaUserDetailsService userDetailsService,
                       CookieService cookieService,
                       CodeService codeService, JpaUserDetailsService jpaUserDetailsService) {

        this.JWT_COOKIE_NAME = jwtCookieName;
        this.jwtsService = jwtsService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.cookieService = cookieService;
        this.codeService = codeService;
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    public String authenticate(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        try {
            checkIfCookieAlreadyExist(request);

            var unauthenticated =
                    UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getPid(), loginDTO.getPassword());

            try {
                this.authenticationManager.authenticate(unauthenticated);
            }catch (Exception e) {
                if(!(e.getCause() instanceof UsernameNotFoundException)) {
                    jpaUserDetailsService.increaseTries(loginDTO.getPid());
                }
                if(e.getCause() instanceof UserBlockedException) {
                    throw new BadCredentialsException(e.getMessage());
                }
                throw new BadCredentialsException("Błędny identyfikator lub hasło");
            }
            jpaUserDetailsService.restartTries(loginDTO.getPid());

            Role userRole = userDetailsService.getUserRole(loginDTO.getPid());

            Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 3);
            String token = Jwts.builder()
                    .setSubject(loginDTO.getPid())
                    .claim("role", userRole.toString())
                    .claim("2fa",false)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(date)//3 min
                    .signWith(jwtsService.getKey(), SignatureAlgorithm.HS256)
                    .compact();

            ResponseCookie jwtCookie = cookieService.generateCookieFromToken(token,date);
            response.addHeader("Set-Cookie", jwtCookie.toString());

            codeService.sendCodeToEmail(loginDTO.getPid());
            return "Wysłano kod na adres email";

        }catch (Exception e){
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private void checkIfCookieAlreadyExist(HttpServletRequest request){
        Optional<Cookie> jwtCookie = cookieService.getCookie(request,JWT_COOKIE_NAME);
        if(jwtCookie.isPresent()){
            String token = jwtCookie.get().getValue();
            if(jwtsService.ifFirstPhaseCompletedReturnClaims(token).isPresent()){
                throw new BadCredentialsException("Użytkownik przeszedł pierwszy stopień uwierzytelniania.");
            }
        }
    }

    public String logout(HttpServletResponse response) {
        try{
            Date date = new Date(System.currentTimeMillis());
            String token = Jwts.builder()
                    .setIssuedAt(date)
                    .setExpiration(date) //0 sec
                    .signWith(jwtsService.getKey(), SignatureAlgorithm.HS256)
                    .compact();

            ResponseCookie jwtCookie = cookieService.generateCookieFromToken(token,date);
            response.addHeader("Set-Cookie", jwtCookie.toString());

            return "Wylogowano";
        } catch (Exception e) {
            throw new RuntimeException("Proces wylogowania nie powiódł się");
        }
    }
}
