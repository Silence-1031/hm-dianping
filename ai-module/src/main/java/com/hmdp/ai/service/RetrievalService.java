package com.hmdp.ai.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.hmdp.ai.document.ShopKnowledgeDocument;
import com.hmdp.ai.dto.RetrievalShop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final EmbeddingModel embeddingModel;
    private final ElasticsearchClient elasticsearchClient;

    @Value("${rag.top-k:5}")
    private Integer topK;

    public List<RetrievalShop> retrieve(String question) {
        try {
            float[] vector = embeddingModel.embed(question);
            SearchResponse<ShopKnowledgeDocument> response = elasticsearchClient.search(s -> s
                            .index("shop_knowledge")
                            .size(topK)
                            .query(q -> q
                                    .bool(b -> b
                                            .should(buildTextQuery(question))
                                            .should(sh -> sh.knn(knn -> knn
                                                    .field("contentVector")
                                                    .queryVector(toList(vector))   // ✅ 直接传 List<Float>
                                                    .k(topK)
                                                    .numCandidates(50)
                                            ))
                                    )
                            ),
                    ShopKnowledgeDocument.class);

            List<RetrievalShop> result = new ArrayList<>();
            response.hits().hits().forEach(hit -> {
                ShopKnowledgeDocument source = hit.source();
                if (source != null) {
                    result.add(RetrievalShop.builder()
                            .shopId(source.getShopId())
                            .name(source.getName())
                            .area(source.getArea())
                            .address(source.getAddress())
                            .openHours(source.getOpenHours())
                            .avgPrice(source.getAvgPrice())
                            .score(source.getScore())
                            .retrieveScore(hit.score() == null ? 0D : hit.score().doubleValue())
                            .content(source.getContent())
                            .build());
                }
            });
            return result;
        } catch (Exception e) {
            log.error("retrieve failed, question={}", question, e);
            return List.of();
        }
    }

    private Query buildTextQuery(String question) {
        return Query.of(q -> q.multiMatch(m -> m
                .query(question)
                .fields("name", "area", "address", "content", "tags")
        ));
    }


    private List<Float> toList(float[] vector) {
        List<Float> list = new ArrayList<>(vector.length);
        for (float v : vector) {
            list.add(v);
        }
        return list;
    }
}