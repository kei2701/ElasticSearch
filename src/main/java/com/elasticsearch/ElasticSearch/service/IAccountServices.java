package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.entity.Account;

import java.io.IOException;
import java.util.List;

public interface IAccountServices {
    Account getAccountById(String accountId) throws IOException;

    List<Account> getAllAccounts() throws IOException;

    String deleteAccount(String accountId) throws IOException;
}
