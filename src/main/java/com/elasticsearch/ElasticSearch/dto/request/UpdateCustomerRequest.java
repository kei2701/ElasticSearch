package com.elasticsearch.ElasticSearch.dto.request;

import lombok.Data;

@Data
public class UpdateCustomerRequest {
    private String fullName;
    private String address;
}
