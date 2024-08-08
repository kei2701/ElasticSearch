package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.entity.Account;
import com.elasticsearch.ElasticSearch.entity.CustomUserDetails;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CustomUserDetailsServicesImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account;
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index(Index.ACCOUNTS)
                    .query(q -> q.term(t -> t.field("username").value(username)))
                    .build();

            SearchResponse<Account> searchResponse = ElasticsearchClientUtil.createClient()
                    .search(searchRequest, Account.class);
            account = searchResponse.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(account);
    }

    public UserDetails loadAccountById(String accountId) throws UsernameNotFoundException, IOException {
        Account account;
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.ACCOUNTS)
                .id(accountId)
                .build();

        GetResponse<Account> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Account.class);
        if (getResponse.found()) {
            account = getResponse.source();
        } else {
            account = null;
        }

        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(account);
    }
}
