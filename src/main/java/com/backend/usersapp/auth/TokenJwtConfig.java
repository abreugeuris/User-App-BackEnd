package com.backend.usersapp.auth;


import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;

public class TokenJwtConfig {
    private TokenJwtConfig() {
    }

    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String USERNAME = "username";
    public static final String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";




}
