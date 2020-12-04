package com.example.jwtauthentication.security;

import com.example.jwtauthentication.exception.AppException;
import com.example.jwtauthentication.payload.JwtAuthenticationResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;
import org.springframework.util.StringUtils;

@Component
public class JwtTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  @Value("${app.jwtExpirationInMs}")
  private Long jwtExpirationInMs;

  @Value("${app.jwtRefSecret}")
  private String jwtRefSecret;

  @Value("${app.jwtRefExpirationInMs}")
  private Long jwtRefExpirationInMs;

  public JwtAuthenticationResponse generateToken(Long userId) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
    Date refExpiryDate = new Date(now.getTime() + jwtRefExpirationInMs);
    return new JwtAuthenticationResponse(signToken(jwtSecret, userId, SignatureAlgorithm.HS384, expiryDate),
                                         signToken(jwtRefSecret,userId, SignatureAlgorithm.HS512, refExpiryDate));
  }

  private String signToken(String secret, Long userId , SignatureAlgorithm signatureAlgorithm, Date expDate) {
      return Jwts.builder()
                 .setSubject(userId.toString())
                 .setIssuedAt(new Date())
                 .setExpiration(expDate)
                 .signWith(signatureAlgorithm, secret)
                 .compact();
  }

  public Long getUserIdFromAccessToken(String accessToken) {
    return getUserIdFromJWT(jwtSecret, accessToken);
  }

  public Long getUserIdFromRefreshToken(String refreshToken) {
    return getUserIdFromJWT(jwtRefSecret, refreshToken);
  }

  public Long getUserIdFromJWT(String secret, String token) {
    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    return Long.parseLong(claims.getSubject());
  }

  public boolean validateAccessToken(String authToken) {
    return validateToken(jwtSecret, authToken);
  }

  public boolean validateToken(String secret, String token) {
    try {
      Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
      return true;
    } catch (SignatureException ex) {
      logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty.");
    }
    return false;
  }

  public JwtAuthenticationResponse generateNewTokens(String refreshToken) {
    if(StringUtils.hasText(refreshToken)){
      validateToken(jwtRefSecret, refreshToken);
      return generateToken(getUserIdFromRefreshToken(refreshToken));
    }
    else
      throw new AppException("Refresh token required!");
  }
}