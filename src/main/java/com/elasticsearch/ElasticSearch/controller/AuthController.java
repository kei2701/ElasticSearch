package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.LoginRequest;
import com.elasticsearch.ElasticSearch.dto.request.RegisterRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.security.JwtProvider;
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
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthController(IAuthServices authServices,
                          AuthenticationManager authenticationManager,
                          JwtProvider jwtProvider) {
        this.authServices = authServices;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws IOException {
        if (!authServices.login(request.getUsername(), request.getPassword()))
            return ResponseEntity.status(401).body(GeneralResponse.builder()
                    .success(false)
                    .message("Login failed!")
                    .statusCode(401)
                    .build());

        return authenticate(request);
    }

    private ResponseEntity<?> authenticate(LoginRequest request) {
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
                .build()
        );
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
