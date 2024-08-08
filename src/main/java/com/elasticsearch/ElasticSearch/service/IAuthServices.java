package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.dto.request.RegisterRequest;
import com.elasticsearch.ElasticSearch.entity.Account;

import java.io.IOException;

public interface IAuthServices {
    boolean login(String username, String password) throws IOException;

    String register(RegisterRequest request) throws IOException;

    Account findAccountByToken(String token) throws IOException;
}
