package com.hmdp.ai.controller;

import com.hmdp.ai.dto.AiChatRequest;
import com.hmdp.ai.dto.AiChatResponse;
import com.hmdp.ai.dto.ApiResult;
import com.hmdp.ai.dto.ReindexResponse;
import com.hmdp.ai.service.RagChatService;
import com.hmdp.ai.service.ShopSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiChatController {

    private final ShopSyncService shopSyncService;
    private final RagChatService ragChatService;

    @PostMapping("/reindex")
    public ApiResult<ReindexResponse> reindex() {
        return ApiResult.ok(shopSyncService.reindexAll());
    }

    @PostMapping("/chat")
    public ApiResult<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return ApiResult.ok(ragChatService.chat(request.getQuestion()));
    }

    @GetMapping("/health")
    public ApiResult<String> health() {
        return ApiResult.ok("ok");
    }
}
