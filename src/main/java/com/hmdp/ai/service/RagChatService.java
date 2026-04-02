package com.hmdp.ai.service;

import com.hmdp.ai.dto.AiChatResponse;
import com.hmdp.ai.dto.RetrievalShop;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RagChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final RetrievalService retrievalService;
    private final String systemPrompt;

    public AiChatResponse chat(String question) {
        List<RetrievalShop> references = retrievalService.retrieve(question);

        String context = references.isEmpty()
                ? "知识库中没有检索到相关商家信息。"
                : references.stream()
                .map(item -> String.format(
                        "商家ID:%s\n商家名称:%s\n商圈:%s\n地址:%s\n营业时间:%s\n人均:%s\n评分:%s\n知识:%s",
                        item.getShopId(),
                        item.getName(),
                        item.getArea(),
                        item.getAddress(),
                        item.getOpenHours(),
                        item.getAvgPrice(),
                        item.getScore() == null ? "未知" : item.getScore() / 10.0,
                        item.getContent()
                ))
                .collect(Collectors.joining("\n\n----------------\n\n"));

        String prompt = "用户问题：\n" + question + "\n\n"
                + "检索到的商家知识库上下文：\n" + context + "\n\n"
                + "请基于这些内容回答。";

        String answer = chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();

        return AiChatResponse.builder()
                .question(question)
                .answer(answer)
                .references(references)
                .build();
    }
}
