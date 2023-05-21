package ru.gadzhiev.mirea_project.services;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис авторизации
 */
@Service
public class AuthService {

    @Value("${secret}")
    private String SECRET;

    private Map<Integer, String> accessTokens;
    private Map<Integer, String> refreshTokens;

    public class TokenData {
        private int employee;
        private String type;
        private long exp;
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getEmployee() {
            return employee;
        }

        public void setEmployee(int employee) {
            this.employee = employee;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getExp() {
            return exp;
        }

        public void setExp(long exp) {
            this.exp = exp;
        }
    }

    public AuthService() {
        accessTokens = new HashMap<>();
        refreshTokens = new HashMap<>();
    }

    public void updateAccessToken(int employee, String token) {
        accessTokens.put(employee, token);
    }

    public void updateRefreshToken(int employee, String token) {
        refreshTokens.put(employee, token);
    }

    public boolean containsAccessToken(int employee, String token) {
        return accessTokens.containsKey(employee) && accessTokens.get(employee).equals(token);
    }

    public boolean containsRefreshToken(int employee, String token) {
        return refreshTokens.containsKey(employee) && refreshTokens.get(employee).equals(token);
    }

    public TokenData generateAccessToken(int employee) {
        TokenData tokenData = new TokenData();
        tokenData.setType("access");
        tokenData.setExp(Instant.now().plus(1, ChronoUnit.MINUTES).getEpochSecond());
        tokenData.setEmployee(employee);

        Map<String, Object> claims = new HashMap<>();
        claims.put("employee", tokenData.getEmployee());
        claims.put("type", tokenData.getType());
        claims.put("exp", tokenData.getExp());

        tokenData.setToken(Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, SECRET).compact());

        return tokenData;
    }

    public TokenData generateRefreshToken(int employee) {
        TokenData tokenData = new TokenData();
        tokenData.setType("refresh");
        tokenData.setExp(Instant.now().plus(8, ChronoUnit.HOURS).getEpochSecond());
        tokenData.setEmployee(employee);

        Map<String, Object> claims = new HashMap<>();
        claims.put("employee", tokenData.getEmployee());
        claims.put("type", tokenData.getType());
        claims.put("exp", tokenData.getExp());

        tokenData.setToken(Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, SECRET).compact());

        return tokenData;
    }

    public TokenData getTokenData(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET).build();
        if(token == null || !parser.isSigned(token))
            return null;

        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            String type = (String) claims.get("type");
            int employee = (Integer) claims.get("employee");
            int exp = (Integer) claims.get("exp");

            TokenData tokenData = new TokenData();
            tokenData.setEmployee(employee);
            tokenData.setExp(exp);
            tokenData.setType(type);
            return tokenData;
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public boolean validateAccessToken(String token) {
        TokenData tokenData = getTokenData(token);
        if(token == null)
            return false;
        return tokenData != null && tokenData.getType().equals("access");
    }

    public boolean validateRefreshToken(String token) {
        TokenData tokenData = getTokenData(token);
        if(token == null)
            return false;
        return tokenData != null && tokenData.getType().equals("refresh");
    }

    public TokenData renewAccess(String token) {
        TokenData tokenData = getTokenData(token);
        if(token == null)
            return null;
        TokenData newToken = generateAccessToken(tokenData.getEmployee());
        updateAccessToken(newToken.getEmployee(), newToken.getToken());
        return newToken;
    }
}
