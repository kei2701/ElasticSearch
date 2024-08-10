package com.elasticsearch.ElasticSearch.util;

import co.elastic.clients.elasticsearch._types.AcknowledgedResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.elasticsearch.ElasticSearch.constant.Index;

import java.io.IOException;

public class IndexUtil {
    public static boolean checkIfIndexExists(String index) throws IOException {
        BooleanResponse result = ElasticsearchClientUtil.createClient().indices()
                .exists(e -> e.index(index));
        return result.value();
    }

    public static void deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
                .index(indexName)
                .build();
        AcknowledgedResponse deleteIndexResponse = ElasticsearchClientUtil.createClient().indices().delete(deleteIndexRequest);
        if (deleteIndexResponse.acknowledged()) {
            System.out.println("Index was successfully deleted");
        } else {
            System.out.println("Index deletion was not acknowledged");
        }
    }
}
