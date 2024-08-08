package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.dto.request.UpdateCustomerRequest;
import com.elasticsearch.ElasticSearch.entity.Customer;

import java.io.IOException;

public interface ICustomerServices {
    Customer getCustomerByToken(String authorization) throws IOException;

    Customer getCustomerById(String customerId) throws IOException;

    String updateCustomer(String authorization, UpdateCustomerRequest request) throws IOException;
}
