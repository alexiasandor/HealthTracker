package com.healthtrack_user.healthtrack_user.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "mbnwF4Z0vb9EdnKeIfmggr5KyYid7vIMQcUkfNwwZHd6g6s9UNdmXtWwsfHyj5ek";
    private static final int jwtTokenPeriod = 24000 * 60 * 60;
    private static final String subjectBegin = "user_logged/";
    private static final Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    public String generateToken(UUID userId, String role) throws InvalidKeyException, IllegalArgumentException {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subjectBegin + role + "/" + userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenPeriod))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    public boolean isTokenValid(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return false;
        }
        token = token.substring(7);

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

            return !isTokenExpired(claims);
        }
        catch (Exception e) {
            return false;
        }
    }
}
