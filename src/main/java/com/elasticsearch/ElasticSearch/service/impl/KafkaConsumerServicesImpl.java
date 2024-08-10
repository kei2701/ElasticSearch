package com.elasticsearch.ElasticSearch.service.impl;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.elasticsearch.ElasticSearch.constant.Index;
import com.elasticsearch.ElasticSearch.constant.Topic;
import com.elasticsearch.ElasticSearch.entity.Post;
import com.elasticsearch.ElasticSearch.service.IKafkaConsumerServices;
import com.elasticsearch.ElasticSearch.util.ElasticsearchClientUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class KafkaConsumerServicesImpl implements IKafkaConsumerServices {

//    @KafkaListener(topics = Topic.POSTS, groupId = "${kafka.consumer.group-id}")
    public void consumeAndCreatePost(String message) throws IOException {
        String id = UUID.randomUUID().toString();
        Post post = new ObjectMapper().readValue(message, Post.class);
        post.setId(id);
        IndexRequest<Post> indexStaffRequest = new IndexRequest.Builder<Post>()
                .id(UUID.randomUUID().toString())
                .index(Index.POSTS)
                .document(post)
                .build();
        IndexResponse response = ElasticsearchClientUtil.createClient().index(indexStaffRequest);
        System.out.println("Create post successfully: " + response.id());
    }
}
