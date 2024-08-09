package com.elasticsearch.ElasticSearch.service;

import com.elasticsearch.ElasticSearch.entity.Post;

import java.io.IOException;
import java.util.List;

public interface IPostServices {
    List<Post> getAllPosts() throws IOException;
}
