package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.dto.request.UpdateStaffRequest;
import com.elasticsearch.ElasticSearch.entity.Account;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.service.IAuthServices;
import com.elasticsearch.ElasticSearch.service.IStaffServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffServicesImpl implements IStaffServices {
    private final IAuthServices authServices;

    public StaffServicesImpl(IAuthServices authServices) {
        this.authServices = authServices;
    }

//    @Override
//    public String indexStaff(Staff staff) throws IOException {
//        staff.setStaffId(UUID.randomUUID().toString());
//        IndexRequest<Staff> request = new IndexRequest.Builder<Staff>()
//                .id(staff.getStaffId())
//                .index(Index.STAFFS)
//                .document(staff)
//                .build();
//        IndexResponse response = ElasticsearchClientUtil.createClient().index(request);
//        return response.id();
//    }

    @Override
    public Staff getStaffById(String staffId) throws IOException {
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.STAFFS)
                .id(staffId)
                .build();

        GetResponse<Staff> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Staff.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public Staff getStaffByToken(String authorization) throws IOException {
        Account account = authServices.findAccountByToken(authorization);
        GetRequest getRequest = new GetRequest.Builder()
                .index(Index.STAFFS)
                .id(account.getUserId())
                .build();

        GetResponse<Staff> getResponse = ElasticsearchClientUtil.createClient().get(getRequest, Staff.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public String updateStaff(String authorization, UpdateStaffRequest request) throws IOException {
        Account account = authServices.findAccountByToken(authorization);
        Staff staff = new Staff();
        staff.setStaffId(account.getUserId());
        staff.setFullName(request.getFullName());
        staff.setAddress(request.getAddress());
        UpdateRequest<Staff, Staff> updateRequest = new UpdateRequest.Builder<Staff, Staff>()
                .index(Index.STAFFS)
                .id(account.getUserId())
                .doc(staff)
                .build();
        UpdateResponse<Staff> updateResponse = ElasticsearchClientUtil.createClient()
                .update(updateRequest, Staff.class);
        return updateResponse.id();
    }

    @Override
    public List<Staff> searchStaffsByFullName(String searchText) throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.STAFFS)
                .query(q -> q.match(t -> t.field("fullName").query(searchText)))
                .build();
        SearchResponse<Staff> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Staff.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public List<Staff> getAllStaffs() throws IOException {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.STAFFS)
                .query(q -> q.matchAll(m -> m))
                .build();
        SearchResponse<Staff> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Staff.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

//    @Override
//    public String deleteStaff(String staffId) throws IOException {
//        DeleteRequest request = new DeleteRequest.Builder()
//                .index(Index.STAFFS)
//                .id(staffId)
//                .build();
//        DeleteResponse response = ElasticsearchClientUtil.createClient().delete(request);
//        return response.result().jsonValue();
//    }

//    public ElasticsearchClient createClient() {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(
//                AuthScope.ANY,
//                new UsernamePasswordCredentials("elastic", "CE4W19ZtP+KB9xS0*qlQ")
//        );
//
//        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200))
//                .setHttpClientConfigCallback(httpClientBuilder ->
//                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
//        RestClient restClient = builder.build();
//
//        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//        return new ElasticsearchClient(transport);
//    }
}
