package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.entity.Province;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.service.IProvinceServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import com.elasticsearch.ElasticSearch.util.ExcelUtil;
import com.elasticsearch.ElasticSearch.util.IndexUtil;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProvinceServicesImpl implements IProvinceServices {
    private final ResourceLoader resourceLoader;

    public ProvinceServicesImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void saveProvinces() throws IOException {
        if(IndexUtil.checkIfIndexExists(Index.PROVINCES)) {
            IndexUtil.deleteIndex(Index.PROVINCES);
        }
        List<Province> provinces = ExcelUtil.readProvinceExcel(resourceLoader
                .getResource("classpath:data/province-list.xls").getURI().getPath());
        System.out.println(provinces);
        IndexRequest<Province> indexRequest;
        for(Province province: provinces) {
             indexRequest = new IndexRequest.Builder<Province>()
                    .id(province.getProvinceId())
                    .index(Index.PROVINCES)
                    .document(province)
                    .build();
            ElasticsearchClientUtil.createClient().index(indexRequest);
        }
    }

    @Override
    public List<Province> getAllProvinces() throws IOException {
        if(!IndexUtil.checkIfIndexExists(Index.PROVINCES)) return null;
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.PROVINCES)
                .query(q -> q.matchAll(m -> m))
                .size(63)
                .build();

        SearchResponse<Province> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Province.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public List<Staff> searchProvinceByProvinceId(String provinceId) throws IOException {
        if(!IndexUtil.checkIfIndexExists(Index.PROVINCES)) return null;

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.PROVINCES)
                .query(q -> q.match(t -> t.field("provinceId").query(provinceId)))
                .build();
        SearchResponse<Staff> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Staff.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}
