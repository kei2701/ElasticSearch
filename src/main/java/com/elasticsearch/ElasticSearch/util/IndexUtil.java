package com.elasticsearch.ElasticSearch.util;

import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.elasticsearch.ElasticSearch.constant.Index;

import java.io.IOException;

public class IndexUtil {
    public static boolean checkIfIndexExists(String index) throws IOException {
        BooleanResponse result = ElasticsearchClientUtil.createClient().indices()
                .exists(e -> e.index(index));
        return result.value();
    }
}
