package com.elasticsearch.ElasticSearch.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName="provinces")
public class Province {
    private String provinceId;
    private String provinceName;
}
