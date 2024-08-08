package com.elasticsearch.ElasticSearch.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName="customers")
public class Customer {
    private String customerId;
    private String fullName;
    private String address;
    private String accountId;
}
