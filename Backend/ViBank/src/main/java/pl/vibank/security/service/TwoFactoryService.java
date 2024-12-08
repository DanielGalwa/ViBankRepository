package pl.vibank.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import pl.vibank.security.model.dto.TwoFactoryAuthDTO;

import java.util.Date;
import java.util.Optional;

@Service
public class TwoFactoryService {

    private final String JWT_COOKIE_NAME;
    private final CookieService cookieService;
    private final JWTsService jwtsService;
    private final CodeService codeService;

    public TwoFactoryService(@Value("${jwt.cookie-name}") String cookieName,CookieService cookieService, JWTsService jwtsService, CodeService codeService) {
        JWT_COOKIE_NAME = cookieName;
        this.cookieService = cookieService;
        this.jwtsService = jwtsService;
        this.codeService = codeService;
    }

    public String authenticate(TwoFactoryAuthDTO twoFactoryAuthDTO, HttpServletRequest request, HttpServletResponse response) {

        Optional<Cookie> jwtCookie = cookieService.getCookie(request,JWT_COOKIE_NAME);
        if(jwtCookie.isPresent()){
            String token = jwtCookie.get().getValue();

            if(jwtsService.ifFirstPhaseCompletedReturnClaims(token).isPresent()) {
                Claims claims = jwtsService.ifFirstPhaseCompletedReturnClaims(token).get();
                String pid = claims.getSubject();
                String role = jwtsService.getClaimsFromToken(token).get("role", String.class);

                if(claims.get("2fa", Boolean.class)){
                    throw new BadCredentialsException("Jesteś już zalogowany!");
                }

                if(codeService.checkIfCodeValid(twoFactoryAuthDTO, pid)){
                    Date date = new Date(System.currentTimeMillis() + 1000 * 60 * 15);//15 minut

                    ResponseCookie csrfCookie = cookieService.generateCsrfToken(date);
                    
                    String twoFactoryAuthToken = Jwts.builder()
                            .setSubject(pid)
                            .claim("role", role)
                            .claim("2fa",true)
                            .claim("csrf", csrfCookie.getValue())
                            .setIssuedAt(new Date(System.currentTimeMillis()))
                            .setExpiration(date)
                            .signWith(jwtsService.getKey(), SignatureAlgorithm.HS256)
                            .compact();

                    ResponseCookie responseCookie = cookieService.generateCookieFromToken(twoFactoryAuthToken,date);
                    response.addHeader("Set-Cookie", csrfCookie.toString());
                    response.addHeader("Set-Cookie", responseCookie.toString());
                }else{
                    throw new BadCredentialsException("Podany kod jest nieprawidłowy lub wygasł. Proszę ponownie się zalogować");
                }
            }else{
                throw new BadCredentialsException("Proszę się zalogować");
            }
        }
        return "Witaj w ViBank";


        //tymczasowo
       // System.out.println(csrfCookie.getValue());
    }
}
