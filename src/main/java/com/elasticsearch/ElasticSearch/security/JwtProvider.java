package com.elasticsearch.ElasticSearch.security;

import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtProvider {
    private final static String JWT_SECRET = "mehG48YDzA9WAv8QcCVIcWLHP2ngZCytqJdef2VW8J7bJty3YER3VwPIu5HjFbGD";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, String> generateJwt(CustomUserDetails customUserDetails) {
        Date today = new Date();
        long JWT_EXPIRATION = 604800000L;
        Date expiredDate = new Date(today.getTime() + JWT_EXPIRATION);

        Map<String, String> result = new HashMap<>();
        String tokenId = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .subject(customUserDetails.getAccount().getAccountId())
                .claim("accountId", customUserDetails.getAccount().getAccountId())
                .claim("tokenId", tokenId)
                .issuedAt(today)
                .expiration(expiredDate)
                .signWith(getSigningKey())
                .compact();
        result.put("token", token);
        result.put("tokenId", tokenId);

        return result;
    }

    public String getAccountIdFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateAuthJwt(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken).getPayload();
            return true;
        } catch (MalformedJwtException ex) {
            throw new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtException(null, null, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new UnsupportedJwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("JWT claims string is empty");
        }
    }
}
