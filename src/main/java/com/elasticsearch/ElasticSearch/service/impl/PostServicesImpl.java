package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.entity.Post;
import com.elasticsearch.ElasticSearch.service.IPostServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import com.elasticsearch.ElasticSearch.util.IndexUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServicesImpl implements IPostServices {
    @Override
    public List<Post> getAllPosts() throws IOException {
        if(!IndexUtil.checkIfIndexExists(Index.POSTS)) return null;
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(Index.POSTS)
                .query(q -> q.matchAll(m -> m))
                .build();

        SearchResponse<Post> searchResponse = ElasticsearchClientUtil.createClient()
                .search(searchRequest, Post.class);
        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}
