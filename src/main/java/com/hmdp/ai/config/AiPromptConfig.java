package com.hmdp.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiPromptConfig {

    @Bean
    public String systemPrompt() {
        return """
                你是黑马点评平台的智能商家导购助手。
                你的任务是严格基于提供的商家知识库内容回答用户问题。
                规则如下：
                1. 只能依据给定上下文回答，不能编造不存在的商家信息。
                2. 如果上下文不足，请明确说明“知识库中没有足够信息”。
                3. 优先输出与用户约束最匹配的商家，并说明原因。
                4. 输出尽量结构化，包含：推荐商家、理由、营业时间、价格、地址。
                5. 如果用户问题是比较型问题，请做对比。
                """;
    }
}
