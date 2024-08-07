package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.elasticsearch.ElasticSearch.dto.request.UpdateStaffRequest;
import com.elasticsearch.ElasticSearch.entity.Staff;
import com.elasticsearch.ElasticSearch.service.IStaffServices;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StaffServicesImpl implements IStaffServices {
    @Override
    public String indexStaff(Staff staff) throws IOException {
        staff.setStaffId(UUID.randomUUID().toString());
        IndexRequest<Staff> request = new IndexRequest.Builder<Staff>()
                .id(staff.getStaffId())
                .index("staffs")
                .document(staff)
                .build();

        ElasticsearchClient client = createClient();
        IndexResponse response = client.index(request);
        return response.id();
    }

    @Override
    public Staff getStaffById(String staffId) throws IOException {
        ElasticsearchClient client = createClient();
        GetRequest getRequest = new GetRequest.Builder()
                .index("staffs")
                .id(staffId)
                .build();

        GetResponse<Staff> getResponse = client.get(getRequest, Staff.class);
        if (getResponse.found()) {
            return getResponse.source();
        } else {
            return null;
        }
    }

    @Override
    public String updateStaff(String staffId, UpdateStaffRequest request) throws IOException {
        ElasticsearchClient client = createClient();
        Staff staff = new Staff();
        staff.setStaffId(staffId);
        staff.setFullName(request.getFullName());
        staff.setAddress(request.getAddress());
        UpdateRequest<Staff, Staff> updateRequest = new UpdateRequest.Builder<Staff, Staff>()
                .index("staffs")
                .id(staffId)
                .doc(staff)
                .build();

        UpdateResponse<Staff> updateResponse = client.update(updateRequest, Staff.class);
        return updateResponse.id();
    }

    @Override
    public List<Staff> searchStaffsByFullName(String searchText) throws IOException {
        ElasticsearchClient client = createClient();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("staffs")
                .query(q -> q.match(t -> t.field("fullName").query(searchText)))
                .build();

        SearchResponse<Staff> searchResponse = client.search(searchRequest, Staff.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public List<Staff> getAllStaffs() throws IOException {
        ElasticsearchClient client = createClient();
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("staffs")
                .query(q -> q.matchAll(m -> m))
                .build();

        SearchResponse<Staff> searchResponse = client.search(searchRequest, Staff.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    @Override
    public String deleteStaff(String staffId) throws IOException {
        ElasticsearchClient client = createClient();
        DeleteRequest request = new DeleteRequest.Builder()
                .index("staffs")
                .id(staffId)
                .build();
        DeleteResponse response = client.delete(request);
        return response.result().jsonValue();
    }

    public ElasticsearchClient createClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "CE4W19ZtP+KB9xS0*qlQ")
        );

        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        RestClient restClient = builder.build();

        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
