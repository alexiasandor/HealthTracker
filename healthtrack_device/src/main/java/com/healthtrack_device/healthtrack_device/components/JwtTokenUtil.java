package com.healthtrack_device.healthtrack_device.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "mbnwF4Z0vb9EdnKeIfmggr5KyYid7vIMQcUkfNwwZHd6g6s9UNdmXtWwsfHyj5ek";
    private static final Key key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

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
