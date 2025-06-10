package com.example.assignment.global.auth.jwt;

import com.example.assignment.domain.user.enums.UserRole;
import com.example.assignment.global.exception.CustomException;
import com.example.assignment.global.exception.ExceptionType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * JWT 토큰 생성 (Bearer prefix와 함께 반환)
     */
    public String createBearerToken(String username, String nickname, UserRole userRole) {
        Date now = new Date();

        return BEARER_PREFIX + Jwts.builder()
                .setSubject(username)
                .claim("nickname", nickname)
                .claim("userRole", userRole)
                .setExpiration(new Date(now.getTime() + TOKEN_TIME))
                .setIssuedAt(now)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    /**
     * Bearer prefix를 제외한 토큰만 추출
     */
    public String extractTokenFromBearerHeader(String bearerHeader) {
        if (bearerHeader != null && bearerHeader.startsWith(BEARER_PREFIX)) {
            return bearerHeader.substring(BEARER_PREFIX.length());
        }
        throw new CustomException(ExceptionType.INVALID_TOKEN, "잘못된 형식의 토큰입니다.");
    }

    /**
     * JWT Claims 추출
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ExceptionType.INVALID_TOKEN, "만료된 토큰입니다.");
        }
        catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ExceptionType.INVALID_TOKEN);
        }
    }
}