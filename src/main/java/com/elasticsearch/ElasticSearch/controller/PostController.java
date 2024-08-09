package com.elasticsearch.ElasticSearch.controller;

import com.elasticsearch.ElasticSearch.dto.response.GeneralResponse;
import com.elasticsearch.ElasticSearch.service.IPostServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final IPostServices postServices;

    public PostController(IPostServices postServices) {
        this.postServices = postServices;
    }

    @GetMapping
    public ResponseEntity<Object> getAllPosts() throws IOException {
        return ResponseEntity.ok(
                GeneralResponse.builder()
                        .success(true)
                        .message("Get all posts successfully")
                        .data(postServices.getAllPosts())
                        .build()
        );
    }
}
