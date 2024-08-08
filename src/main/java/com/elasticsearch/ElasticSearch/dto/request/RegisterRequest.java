package com.elasticsearch.ElasticSearch.dto.request;

import com.elasticsearch.ElasticSearch.constant.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String confirmPassword;
    private Role role;
    private String fullName;
    private String address;
}
