package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.IProvinceServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/provinces")
public class ProvinceController {

    private final IProvinceServices provinceServices;

    public ProvinceController(IProvinceServices provinceServices) {
        this.provinceServices = provinceServices;
    }

    @PostMapping
    public ResponseEntity<Object> saveProvinces() throws IOException {
        provinceServices.saveProvinces();
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Save all provinces successfully")
                        .data(null)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getAllProvinces() throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get all provinces successfully")
                        .data(provinceServices.getAllProvinces())
                        .build()
        );
    }
}
