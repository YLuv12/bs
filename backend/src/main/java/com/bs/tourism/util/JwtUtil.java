package com.bs.tourism.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "YmFpc2VUb3VyaXNtU2VjcmV0S2V5MjAyNE11c3RCZUF0TGVhc3QyNTZCaXRz";
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    private static SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String createToken(Integer userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }

    public static Integer getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
