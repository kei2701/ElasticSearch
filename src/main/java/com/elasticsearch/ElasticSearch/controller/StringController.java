package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.IStringServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/string")
public class StringController {

    private final IStringServices stringServices;

    public StringController(IStringServices stringServices) {
        this.stringServices = stringServices;
    }

    @PostMapping("/validate-string")
    public ResponseEntity<Object> validateString(@RequestParam String stringNumber) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message(stringServices.validateString(stringNumber))
                        .data(null)
                        .build()
        );
    }
}
