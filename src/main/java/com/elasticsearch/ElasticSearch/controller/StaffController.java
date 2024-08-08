package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.request.UpdateStaffRequest;
import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.IStaffServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/staffs")
@PreAuthorize("hasRole('ROLE_STAFF')")
public class StaffController {

    private final IStaffServices staffServices;

    public StaffController(IStaffServices staffServices) {
        this.staffServices = staffServices;
    }

//    @PostMapping("/index")
//    public ResponseEntity<Object> saveStaff(@RequestBody Staff staff) throws IOException {
//        return ResponseEntity.ok(
//                GeneralResponse.builder()
//                        .success(true)
//                        .message("Create staff successfully")
//                        .data(staffServices.indexStaff(staff))
//                        .build()
//        );
//    }

    @GetMapping("/{staffId}")
    public ResponseEntity<Object> getStaffById(@PathVariable("staffId")  String staffId) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get staff by id successfully")
                        .data(staffServices.getStaffById(staffId))
                        .build()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getStaffsByFullName(@RequestParam String searchText) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get staffs by full name successfully")
                        .data(staffServices.searchStaffsByFullName(searchText))
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Object> getAllStaffs() throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get all staffs successfully")
                        .data(staffServices.getAllStaffs())
                        .build()
        );
    }

    @PutMapping
    public ResponseEntity<Object> updateStaff(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UpdateStaffRequest request
    ) throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Update staff successfully")
                        .data(staffServices.updateStaff(authorization, request))
                        .build()
        );
    }

//    @DeleteMapping("/{staffId}")
//    public ResponseEntity<Object> deleteStaff(@PathVariable("staffId")  String staffId) throws IOException {
//        return ResponseEntity.ok(
//                GeneralResponse.builder()
//                        .success(true)
//                        .message("Delete staff successfully")
//                        .data(staffServices.deleteStaff(staffId))
//                        .build()
//        );
//    }
}
