package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.LoginRequest;
import com.elasticsearch.ElasticSearch.dto.request.RegisterRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.security.JwtAuthenticator;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) throws IOException {
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
