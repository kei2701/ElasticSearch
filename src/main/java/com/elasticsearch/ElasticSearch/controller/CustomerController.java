package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.UpdateCustomerRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.ICustomerServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('ROLE_CUSTOMER')")
public class CustomerController {

    private final ICustomerServices customerServices;

    public CustomerController(ICustomerServices customerServices) {
        this.customerServices = customerServices;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Object> getCustomerById(@PathVariable("customerId")  String customerId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get customer by id successfully")
                        .data(customerServices.getCustomerById(customerId))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getCustomerByToken(
            @RequestHeader("Authorization") String authorization
    ) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get customer by token successfully")
                        .data(customerServices.getCustomerByToken(authorization))
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<Object> updateCustomer(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdateCustomerRequest request
    ) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Update customer successfully")
                        .data(customerServices.updateCustomer(authorization, request))
                        .build()
        );
    }
}
