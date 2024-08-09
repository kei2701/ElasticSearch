package com.elasticsearch.ElasticSearch.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName="posts")
public class Post {
    private String id;
    private String title;
    private String body;
    private String userId;
}
