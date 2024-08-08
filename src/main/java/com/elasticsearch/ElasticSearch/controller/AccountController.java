package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.IAccountServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final IAccountServices accountServices;

    public AccountController(IAccountServices accountServices) {
        this.accountServices = accountServices;
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    public ResponseEntity<Object> getAccountById(@PathVariable("accountId")  String accountId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get account by id successfully")
                        .data(accountServices.getAccountById(accountId))
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_STAFF')")
    public ResponseEntity<Object> getAllAccounts() throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get all accounts successfully")
                        .data(accountServices.getAllAccounts())
                        .build()
        );
    }

    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    public ResponseEntity<Object> deleteAccount(@PathVariable("accountId")  String accountId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Delete account successfully")
                        .data(accountServices.deleteAccount(accountId))
                        .build()
        );
    }
}
