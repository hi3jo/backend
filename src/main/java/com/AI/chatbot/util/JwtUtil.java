package com.AI.chatbot.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key";

    public static String generateToken(String userid, String nickname) {
        return Jwts.builder()
                .claim("userid", userid)
                .claim("nickname", nickname)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenValid(String token) {
        return extractClaims(token).getExpiration().after(new Date());
    }

    public static String extractUserid(String token) {
        return extractClaims(token).get("userid", String.class);
    }

    public static String extractNickname(String token) {
        return extractClaims(token).get("nickname", String.class);
    }
}
