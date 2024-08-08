package com.elasticsearch.ElasticSearch.entity;

import com.elasticsearch.ElasticSearch.constant.Role;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName="accounts")
public class Account {
    private String accountId;
    private String username;
    private String password;
    private Role role;
    private boolean isDeleted = false;
    private String userId;
}
