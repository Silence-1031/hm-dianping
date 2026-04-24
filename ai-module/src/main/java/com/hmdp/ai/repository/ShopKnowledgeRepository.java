package com.hmdp.ai.repository;

import com.hmdp.ai.document.ShopKnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ShopKnowledgeRepository extends ElasticsearchRepository<ShopKnowledgeDocument, String> {
}