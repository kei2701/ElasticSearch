package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.UpdateStaffRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.service.IStaffServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/elastic-search")
public class StaffController {

    private final IStaffServices elasticSearchServices;

    public StaffController(IStaffServices elasticSearchServices) {
        this.elasticSearchServices = elasticSearchServices;
    }

    @PostMapping("/index")
    public ResponseEntity<Object> saveStaff(@RequestBody Staff staff) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Create staff successfully")
                        .data(elasticSearchServices.indexStaff(staff))
                        .build()
        );
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<Object> getStaffById(@PathVariable("staffId")  String staffId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get staff by id successfully")
                        .data(elasticSearchServices.getStaffById(staffId))
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getStaffsByFullName(@RequestParam String searchText) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get staffs by full name successfully")
                        .data(elasticSearchServices.searchStaffsByFullName(searchText))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getAllStaffs() throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get all staffs successfully")
                        .data(elasticSearchServices.getAllStaffs())
                        .build()
        );
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<Object> updateStaff(
            @PathVariable("staffId") String staffId,
            @RequestBody UpdateStaffRequest request
    ) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Update staff successfully")
                        .data(elasticSearchServices.updateStaff(staffId, request))
                        .build()
        );
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Object> deleteStaff(@PathVariable("staffId")  String staffId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get staff by id successfully")
                        .data(elasticSearchServices.deleteStaff(staffId))
                        .build()
        );
    }
}
