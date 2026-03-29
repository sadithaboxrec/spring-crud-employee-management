package com.test.employee.service;

import com.test.employee.dto.TokenPair;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j  // for our class to be login
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private static final String TOKEN_PREFIX = "Bearer ";

       // after  JwtService TokenPair method
    public TokenPair generateTokenPair(Authentication authentication) {

        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);

        return new TokenPair(accessToken,refreshToken);
    }


    // Generate Access Token
    public String generateAccessToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Get the role from authorities
        String role = userPrincipal.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        return Jwts.builder()
                .header()
                .add("typ","JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

//    Generate Refresh Token

    public String generateRefreshToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        Map<String, String> claims = new HashMap<>();
        claims.put("tokenType","refresh");


        // Get the role from authorities
        String role = userPrincipal.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        return Jwts.builder()
                .header()
                .add("typ","JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .claim("role", role)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }




    // validate token

    public boolean validateTokenForUsers(String token, UserDetails userDetails) {

        final String username= extractUsernameFromToken(token);   // Extract username from token

        return username !=null
                && username.equals((userDetails.getUsername()));


//        if (!username.equals(userDetails.getUsername())) {
//            return false;
//        }

//        try{
//            Jwts.parser()
//                    .verifyWith(getSignInKey())
//                    .build()
//                    .parseSignedClaims(token);
//            return true;
//        }catch (SignatureException e){
//            log.error("Invalid JWT signature: {}", e.getMessage());
//        }catch (MalformedJwtException e){
//            log.error("Invalid JWT token: {}", e.getMessage());
//        }catch (ExpiredJwtException e){
//            log.error("Expired JWT token: {}", e.getMessage());
//        }catch (UnsupportedJwtException e){
//            log.error("Unsupported JWT token: {}", e.getMessage());
//        }catch (IllegalArgumentException e){
//            log.error("JWT claims string is empty: {}", e.getMessage());
//        }
//        return false;

    }

    public boolean validateToken(String token) {

        return extractAllClaims(token) != null;
    }



    // validate if the token is refresh token

    public boolean isRefreshToken(String token) {

        Claims claims = extractAllClaims(token);

        if(claims == null){
            return false;
        }

//        Claims claims = Jwts.parser()
//                .verifyWith(getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();

        return "refresh".equals(claims.get("tokenType"));

    }

    private Claims extractAllClaims(String token) {

        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }

        return claims;
    }


    //    private String extractUsernameFromToken(String token) {
     public String extractUsernameFromToken(String token){

        Claims claims = extractAllClaims(token);

        if(claims != null){
            return claims.getSubject();
        }

//          return Jwts.parser()
//                .verifyWith(getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getSubject();

         return null;
     }



}
