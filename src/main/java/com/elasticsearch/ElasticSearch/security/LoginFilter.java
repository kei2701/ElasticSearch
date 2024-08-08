package com.elasticsearch.ElasticSearch.security;

import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class LoginFilter extends OncePerRequestFilter {

    @Autowired
    private IAuthServices authServices;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            if(requestURI.contains("/auth/login")) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), new TypeReference<>() {
                });
                String username = requestBody.get("username");
                String password = requestBody.get("password");
                if (!authServices.login(username, password)) {
                    throw new AuthenticationException("Invalid login credentials");
                }
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username,password)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                Map<String, String> result = jwtProvider.generateJwt((CustomUserDetails) authentication.getPrincipal());
                String token = result.get("token");
                request.setAttribute("token", token);
            }
        } catch (AuthenticationException ex) {
            response.setStatus(403);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid login credentials\", \"result\": \"Please login again!\", \"statusCode\": \"403\"}");
            out.flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
