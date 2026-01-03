package kr.co.ksgk.ims.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.ksgk.ims.domain.auth.dto.CustomUserDetails;
import kr.co.ksgk.ims.domain.auth.dto.AuthDto;
import kr.co.ksgk.ims.domain.member.entity.Role;
import kr.co.ksgk.ims.global.error.ErrorCode;
import kr.co.ksgk.ims.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getAccessExpirationTime());
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, jwtProperties.getRefreshExpirationTime());
    }

    public String generateAttendanceToken(Long memberId) {
        return Jwts.builder()
                .claim("memberId", memberId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAttendanceExpirationTime()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(Authentication authentication, long expirationTime) {
        String memberId = authentication.getName();
        String roleStr = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.INVALID_AUTHORITY));
        return Jwts.builder()
                .claim("memberId", Long.parseLong(memberId))
                .claim("role", roleStr)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public void validateAccessToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN_VALUE);
        }
    }

    public void validateRefreshToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN_VALUE);
        }
    }

    public Long validateAttendanceToken(String token) {
        try {
            return getJwtParser().parseSignedClaims(token).getPayload().get("memberId", Long.class);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_ATTENDANCE_TOKEN);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ATTENDANCE_TOKEN_VALUE);
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getJwtParser().parseSignedClaims(token).getPayload();
        Long memberId = claims.get("memberId", Long.class);
        String roleStr = claims.get("role", String.class); // "ROLE_ADMIN"
        Role role = Role.valueOf(roleStr.replace("ROLE_", "")); // enum Role.ADMIN
        AuthDto authDto = AuthDto.of(memberId, role);
        UserDetails principal = new CustomUserDetails(authDto);
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }
}