package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.*;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.dto.request.UpdateCustomerRequest;
import com.elasticsearch.ElasticSearch.entity.Account;
import com.elasticsearch.ElasticSearch.entity.Customer;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import com.elasticsearch.ElasticSearch.service.ICustomerServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CustomerServicesImpl implements ICustomerServices {
    private final IAuthServices authServices;

    public CustomerServicesImpl(IAuthServices authServices) {
        this.authServices = authServices;
    }

    @Override
    public Customer getCustomerByToken(String authorization) throws IOException {
        Account account = authServices.findAccountByToken(authorization);
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.CUSTOMERS)
                .id(account.getUserId())
                .build();

        GetResponse<Customer> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Customer.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public Customer getCustomerById(String customerId) throws IOException {
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.CUSTOMERS)
                .id(customerId)
                .build();

        GetResponse<Customer> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Customer.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public String updateCustomer(String authorization, UpdateCustomerRequest request) throws IOException {
        Account account = authServices.findAccountByToken(authorization);
        Customer customer = new Customer();
        customer.setCustomerId(account.getUserId());
        customer.setFullName(request.getFullName());
        customer.setAddress(request.getAddress());
        UpdateRequest<Customer, Customer> updateRequest = new UpdateRequest.Builder<Customer, Customer>()
                .index(Index.CUSTOMERS)
                .id(customer.getCustomerId())
                .doc(customer)
                .build();
        UpdateResponse<Customer> updateResponse = ElasticsearchClientUtil.createClient()
                .update(updateRequest, Customer.class);
        return updateResponse.id();
    }
}
