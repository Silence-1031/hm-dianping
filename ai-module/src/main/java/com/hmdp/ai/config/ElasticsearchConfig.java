package com.hmdp.ai.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DenseVectorSimilarity;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticsearchConfig {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void initIndex() throws IOException {
        String indexName = "shop_knowledge";

        // 1. 判断索引是否存在
        boolean exists = elasticsearchClient.indices().exists(
                ExistsRequest.of(e -> e.index(indexName))
        ).value();

        if (exists) {
            log.info("ES index [{}] already exists", indexName);
            return;
        }

        // 2. 构建 8.x 新版 mappings（直接用 Map<String, Property>）
        Map<String, Property> properties = new HashMap<>();

        properties.put("shopId", Property.of(p -> p.long_(l -> l)));
        properties.put("name", Property.of(p -> p.text(t -> t)));
        properties.put("typeId", Property.of(p -> p.long_(l -> l)));
        properties.put("area", Property.of(p -> p.keyword(k -> k)));
        properties.put("address", Property.of(p -> p.text(t -> t)));
        properties.put("openHours", Property.of(p -> p.keyword(k -> k)));
        properties.put("avgPrice", Property.of(p -> p.long_(l -> l)));
        properties.put("score", Property.of(p -> p.integer(i -> i)));
        properties.put("sold", Property.of(p -> p.integer(i -> i)));
        properties.put("comments", Property.of(p -> p.integer(i -> i)));
        properties.put("tags", Property.of(p -> p.keyword(k -> k)));
        properties.put("content", Property.of(p -> p.text(t -> t)));
        properties.put("contentVector", Property.of(p -> p.denseVector(v -> v
                .dims(1024)
                .index(true)
                .similarity(DenseVectorSimilarity.Cosine)
        )));
        properties.put("updatedAt", Property.of(p -> p.date(d -> d)));

        // 3. 构建 settings
        IndexSettings settings = IndexSettings.of(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
        );

        // 4. 创建索引（8.x 标准写法，一步到位：settings + mappings）
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
                .index(indexName)
                .settings(settings)
                .mappings(m -> m.properties(properties))
        );

        elasticsearchClient.indices().create(request);
        log.info("ES index [{}] created successfully", indexName);
    }
}