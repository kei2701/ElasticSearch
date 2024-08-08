package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.LoginRequest;
import com.elasticsearch.ElasticSearch.dto.request.RegisterRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.security.JwtAuthenticator;
import com.elasticsearch.ElasticSearch.security.JwtProvider;
import com.elasticsearch.ElasticSearch.security.SecurityConfig;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthServices authServices;
    private final JwtAuthenticator jwtAuthenticator;

    public AuthController(IAuthServices authServices, JwtAuthenticator jwtAuthenticator) {
        this.authServices = authServices;
        this.jwtAuthenticator = jwtAuthenticator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws IOException {
        if (!authServices.login(request.getUsername(), request.getPassword())) {
            return ResponseEntity.status(401).body(GeneralResponse.builder()
                    .success(false)
                    .message("Login failed!")
                    .statusCode(401)
                    .build());
        }

        Map<String, String> result = jwtAuthenticator.authenticate(request);

        return ResponseEntity.ok(GeneralResponse.builder()
                .success(true)
                .message("Login success")
                .data(result.get("token"))
                .statusCode(200)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Register successfully")
                        .data(authServices.register(request))
                        .build()
        );
    }
}
