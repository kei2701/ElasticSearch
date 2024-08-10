package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.entity.Province;
import com.elasticsearch.ElasticSearch.entity.Staff;

import java.io.IOException;
import java.util.List;

public interface IProvinceServices {
    void saveProvinces() throws IOException;

    List<Province> getAllProvinces() throws IOException;

    List<Staff> searchProvinceByProvinceId(String provinceId) throws IOException;
}
