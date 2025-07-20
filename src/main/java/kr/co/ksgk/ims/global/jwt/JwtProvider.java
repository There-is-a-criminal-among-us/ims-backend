package kr.co.ksgk.ims.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.util.Date;

@Component
public class JwtProvider
{

    private final String secretKey = "my-secret-key";
    private final long expirationTime = 1000 * 60 * 60;

    public String createToken(Long memberId)
    {
        return Jwts.builder()
                .setSubject(memberId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public boolean validate(String token)
    {
        try
        {
            parseClaims(token);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public Claims parseClaims(String token)
    {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }
}