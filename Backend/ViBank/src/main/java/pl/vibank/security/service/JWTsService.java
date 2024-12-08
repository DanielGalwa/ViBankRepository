package pl.vibank.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class JWTsService {

    private final JpaUserDetailsService jpaUserDetailsService;
    private final Key secret;

    public JWTsService(@Value("${jwt.secret}") String generatedSecretKey, JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(generatedSecretKey);
        this.secret = Keys.hmacShaKeyFor(keyBytes);
    }

    public Key getKey(){
        return this.secret;
    }


    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<Claims> ifFirstPhaseCompletedReturnClaims(String token){

        try{
            Claims claims = getClaimsFromToken(token);

            String pid = claims.getSubject();

            if(claims.get("role", String.class) == null){
                return Optional.empty();
            }

            if(claims.get("2fa", Boolean.class) == null){
                return Optional.empty();
            }

            if(pid == null){
                return Optional.empty();
            }

            jpaUserDetailsService.loadUserByUsername(pid);

            if(claims.getExpiration().before(new Date())){
                return Optional.empty();
            }

            return Optional.of(claims);

        }catch (Exception e){
            return Optional.empty();
        }
    }
}
