package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.entity.Account;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.service.IAccountServices;
import com.elasticsearch.ElasticSearch.service.IStaffServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServicesImpl implements IAccountServices {
    private final IStaffServices staffServices;

    public AccountServicesImpl(IStaffServices staffServices) {
        this.staffServices = staffServices;
    }

    @Override
    public Account getAccountById(String accountId) throws IOException {
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.ACCOUNTS)
                .id(accountId)
                .build();

        GetResponse<Account> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Account.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public List<Account> getAllAccounts() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.ACCOUNTS)
                .query(q -> q.matchAll(m -> m))
                .build();

        SearchResponse<Account> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Account.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteAccount(String accountId) throws IOException {
        Account account = getAccountById(accountId);
        Staff staff = staffServices.getStaffById(account.getUserId());

        DeleteRequest staffRequest = new DeleteRequest.Builder()
                .index(Index.STAFFS)
                .id(staff.getStaffId())
                .build();
        ElasticsearchClientUtil.createClient().delete(staffRequest);

        DeleteRequest accountRequest = new DeleteRequest.Builder()
                .index(Index.ACCOUNTS)
                .id(accountId)
                .build();
        DeleteResponse response = ElasticsearchClientUtil.createClient().delete(accountRequest);

        return response.result().jsonValue();
    }
}
