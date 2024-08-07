package com.elasticsearch.ElasticSearch.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;


@Data
@Document(indexName="staffs")
public class Staff {
    private String staffId;
    private String fullName;
    private String address;
}
