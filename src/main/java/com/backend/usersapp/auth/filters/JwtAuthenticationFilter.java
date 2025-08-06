package com.backend.usersapp.auth.filters;

import static com.backend.usersapp.auth.TokenJwtConfig.*;

import com.backend.usersapp.models.entities.Role;
import com.backend.usersapp.models.entities.UserApp;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UserApp userApp;
        String username = null;
        String password = null;

        try {
            userApp = new ObjectMapper().readValue(request.getInputStream(), UserApp.class);
            username = userApp.getUsername();
            password = userApp.getPassword();

            logger.info("Attempting to authenticate user: " + username);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            logger.error("Failed to parse request body {}", e);
        }


        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String username = ((User) authResult.getPrincipal()).getUsername();

        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        boolean isAdmin = roles.contains(Role.ROLE_ADMIN);

        Claims claims = Jwts.claims()
                .add("authorities", new ObjectMapper().writeValueAsString(roles))
                .add("isAdmin", isAdmin)
                .build();



        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .signWith(SECRET_KEY)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3_600_000L))
                .compact();

        response.addHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + token);
        Map<String, Object> body = new HashMap<>();
        body.put(USERNAME, username);
        body.put(TOKEN, token);
        body.put(MESSAGE, String.format("Successfully authenticated user: %s", username));

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_OK);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Map<String, Object> body = new HashMap<>();
        body.put(MESSAGE, "Username or password is incorrect");
        body.put(ERROR, failed.getMessage());
        response.setContentType("application/json");

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}

