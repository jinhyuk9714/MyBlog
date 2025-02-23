package com.example.myblog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY = EnvConfig.get("JWT_SECRET_KEY");
    private final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7일

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY); // Base64 인코딩된 키 사용
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Access Token 생성
    public String generateAccessToken(String username, Set<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(ACCESS_TOKEN_EXPIRATION)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Refresh Token 생성
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰에서 사용자명 추출
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ JWT에서 roles를 Set<String>으로 변환
    public Set<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof Collection<?>) {
            return ((Collection<?>) rolesObject).stream()
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 리프레시 토큰의 유효 시간(밀리초) 반환
     */
    public long getRefreshTokenExpiration() { // ✅ 메서드 이름 일관되게 변경
        return REFRESH_TOKEN_EXPIRATION;
    }

}
