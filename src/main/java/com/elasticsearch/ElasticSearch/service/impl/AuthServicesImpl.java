package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.constant.Role;
import com.elasticsearch.ElasticSearch.dto.request.RegisterRequest;
import com.elasticsearch.ElasticSearch.entity.Account;
import com.elasticsearch.ElasticSearch.entity.Customer;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.security.JwtProvider;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import com.elasticsearch.ElasticSearch.util.IndexUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServicesImpl implements IAuthServices {
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthServicesImpl(PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean login(String username, String password) throws IOException {
        Optional<Account> accountOptional = searchAccountByUsername(username);

        return accountOptional.filter(account -> passwordEncoder.matches(password, account.getPassword())).isPresent();

    }

    @Override
    public String register(RegisterRequest registerRequest) throws IOException {
        Optional<Account> accountOptional = searchAccountByUsername(registerRequest.getUsername());
        Account account;
        if(accountOptional.isPresent()) {
            throw new BadRequestException("Account already exist");
        } else {
            account = new Account();
        }
        account.setAccountId(UUID.randomUUID().toString());
        account.setUsername(registerRequest.getUsername());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(registerRequest.getRole());
        account.setDeleted(false);

        Object user;
        String userId = UUID.randomUUID().toString();
        if(Role.STAFF.equals(registerRequest.getRole())) {
            user = new Staff();
            ((Staff) user).setStaffId(userId);
            ((Staff) user).setFullName(registerRequest.getFullName());
            ((Staff) user).setAddress(registerRequest.getAddress());
            ((Staff) user).setAccountId(account.getAccountId());
            IndexRequest<Staff> indexStaffRequest = new IndexRequest.Builder<Staff>()
                    .id(((Staff) user).getStaffId())
                    .index(Index.STAFFS)
                    .document((Staff) user)
                    .build();
            ElasticsearchClientUtil.createClient().index(indexStaffRequest);
        } else {
            user = new Customer();
            ((Customer) user).setCustomerId(userId);
            ((Customer) user).setFullName(registerRequest.getFullName());
            ((Customer) user).setAddress(registerRequest.getAddress());
            ((Customer) user).setAccountId(account.getAccountId());
            IndexRequest<Customer> indexStaffRequest = new IndexRequest.Builder<Customer>()
                    .id(((Customer) user).getCustomerId())
                    .index(Index.CUSTOMERS)
                    .document((Customer) user)
                    .build();
            ElasticsearchClientUtil.createClient().index(indexStaffRequest);
        }

        account.setUserId(userId);

        IndexRequest<Account> indexAccountRequest = new IndexRequest.Builder<Account>()
                .id(account.getAccountId())
                .index(Index.ACCOUNTS)
                .document(account)
                .build();
        IndexResponse response = ElasticsearchClientUtil.createClient().index(indexAccountRequest);

        return response.id();
    }

    public Optional<Account> searchAccountByUsername(String searchText) throws IOException {
        boolean exists = IndexUtil.checkIfIndexExists(Index.ACCOUNTS);

        if(!exists) {
            return Optional.empty();
        }

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.ACCOUNTS)
                .query(q -> q.term(t -> t.field("username").value(searchText)))
                .build();

        SearchResponse<Account> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Account.class);
        return searchResponse.hits().hits().stream()
                .findFirst()
                .map(Hit::source);
    }

    @Override
    public Account findAccountByToken(String token) throws IOException {
        if(!IndexUtil.checkIfIndexExists(Index.ACCOUNTS)) return null;

        token = token.substring(7);
        boolean isValid = false;
        try {
            isValid = jwtProvider.validateAuthJwt(token);
        } catch (Exception ignored) {
        }
        if (!isValid) {
            return null;
        }

        String accountId = jwtProvider.getAccountIdFromJwt(token);
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
            throw new AccessDeniedException("Token is not valid");
        }

        return account;
    }
}
