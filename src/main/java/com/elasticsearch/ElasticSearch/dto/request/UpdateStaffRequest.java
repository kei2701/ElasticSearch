package com.elasticsearch.ElasticSearch.dto.request;

import lombok.Data;

@Data
public class UpdateStaffRequest {
    private String fullName;
    private String address;
}
