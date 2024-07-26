package com.beyond.ordersystem.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class jwtTokenprovider {

    @Value("${jwt.secretKey}")
    private  String secretKey;

    @Value("${jwt.expiration}")
    private  int expiration;

    public String createToken(String email, String role){
        // Claims : 사용자 정보이자 payload 정보
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)// 생성 시간
                .setExpiration(new Date(now.getTime() +  30*60*1000L)) //만료 시간 : 30분
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        return token;
    }
}
