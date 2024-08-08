package com.elasticsearch.ElasticSearch.security;

import com.elasticsearch.ElasticSearch.dto.request.LoginRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticator {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final IAuthServices authServices;

    public JwtAuthenticator(AuthenticationManager authenticationManager,
                            JwtProvider jwtProvider,
                            IAuthServices authServices) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.authServices = authServices;
    }

    public ResponseEntity<?> authenticate(LoginRequest request) throws IOException {
        if (!authServices.login(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(401).body(GeneralResponse.builder()
                    .success(false)
                    .message("Login failed!")
                    .statusCode(401)
                    .build());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, String> result = jwtProvider.generateJwt(
                (CustomUserDetails) authentication.getPrincipal()
        );

        return ResponseEntity.ok(GeneralResponse.builder()
                .success(true)
                .message("Login success")
                .data(result.get("token"))
                .statusCode(200)
                .build());
    }
}
