package com.backend.usersapp.auth.filters;

import static com.backend.usersapp.auth.TokenJwtConfig.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JwtValidationFilter extends BasicAuthenticationFilter {

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {


        String header = request.getHeader(HEADER_AUTHORIZATION);
        if (Objects.isNull(header) || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            String token = header.replace(TOKEN_PREFIX, "");


            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            Object authoritiesClaims = claims.get("authorities");


            List<String> roles = new ObjectMapper()
                    .readValue(authoritiesClaims.toString(), new TypeReference<>() {});

            List<GrantedAuthority> grantedAuthorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);

        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put(MESSAGE, "The token JWT invalid ");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setContentType("application/json");
        }
    }
}
