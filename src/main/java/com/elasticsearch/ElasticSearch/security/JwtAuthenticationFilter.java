package com.elasticsearch.ElasticSearch.security;

import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import com.elasticsearch.ElasticSearch.service.impl.CustomUserDetailsServicesImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private CustomUserDetailsServicesImpl customUserDetailsServices;
    @Autowired
    private IAuthServices authServices;

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

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
                    throw new AuthenticationException("Invalid login credentials!");
                }
            }
            String jwt = getJwtFromRequest(request);
            if (jwt != null && jwtProvider.validateAuthJwt(jwt)) {
                String accountId = jwtProvider.getAccountIdFromJwt(jwt);

                CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsServices.loadAccountById(accountId);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (AuthenticationException ex) {
            response.setStatus(403);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Invalid login credentials\", \"result\": \"Please login again!\", \"statusCode\": \"403\"}");
            out.flush();
            return;
        } catch (Exception ex) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": false, \"message\": \"Access Denied\", \"result\": \"Please login again!\", \"statusCode\": \"401\"}");
            out.flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
