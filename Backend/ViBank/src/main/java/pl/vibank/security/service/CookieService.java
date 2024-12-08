package pl.vibank.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;


@Service
public class CookieService {

    private final String JWT_COOKIE_NAME;
    private final SecureRandom secureRandom;
    private static final int TOKEN_BYTE_SIZE = 24;

    public CookieService(@Value("${jwt.cookie-name}") String jwtCookieName, SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
        this.JWT_COOKIE_NAME = jwtCookieName;
    }

    public Optional<Cookie> getCookie(HttpServletRequest request, String nameOfCookie) {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
           return Optional.empty();
        }


        Cookie definedCookie = Arrays.stream(cookies)
                .filter(cookie -> nameOfCookie.equals(cookie.getName()))
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(definedCookie);
    }

    public ResponseCookie generateCookieFromToken(String token, Date date) {
        long maxAge = (date.getTime() - System.currentTimeMillis()) / 1000;//bo jak jest date.getTime to 2025 sie ustawia
        return ResponseCookie.from(JWT_COOKIE_NAME, token)
                .httpOnly(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie generateCsrfToken(Date date) {
        long maxAge = (date.getTime() - System.currentTimeMillis()) / 1000;

        byte[] tokenBytes = new byte[TOKEN_BYTE_SIZE];
        secureRandom.nextBytes(tokenBytes);
        String csrfToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        return ResponseCookie.from("CSRFcookie",csrfToken)
                .httpOnly(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}
