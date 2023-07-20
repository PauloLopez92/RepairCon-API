package com.singlesoft.web.jwt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singlesoft.web.jwt.CustomUserDetails;
import com.singlesoft.web.model.AuthResponse;
import com.singlesoft.web.model.User;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthFilter extends UsernamePasswordAuthenticationFilter {

	// To set 1 Min
    //public static final int TOKEN_TIME = 60_000;

	// To set 10 Min
    public static final int TOKEN_TIME = 3_600_000;

	// To set 24h
    //public static final int TOKEN_TIME = 86_400_000;

    public static final String TOKEN_PASS = "650b0c55-1c18-4cc9-82b2-d4d79862a94a";

    private final AuthenticationManager authenticationManager;

    public JWTAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper()
                    .readValue(request.getInputStream(), User.class);

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    user.getName(),
                    user.getPassword(),
                    new ArrayList<>()
            ));

        } catch (IOException e) {
            throw new RuntimeException("ERROR AUTH!!!", e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        CustomUserDetails customUser = (CustomUserDetails) authResult.getPrincipal();

        String token = JWT.create().
                withSubject(customUser.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TOKEN_TIME))
                .withClaim("userId", customUser.getId())
                .withClaim("userType", customUser.getType())
                .sign(Algorithm.HMAC512(TOKEN_PASS));

        //response.getWriter().write(token);
        //response.getWriter().flush();

        AuthResponse authResponse = new AuthResponse(token);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(authResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
