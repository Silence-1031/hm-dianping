package com.hmdp.ai.service;

import com.hmdp.ai.document.ShopKnowledgeDocument;
import com.hmdp.ai.dto.ShopDTO;
import com.hmdp.ai.dto.VoucherDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ShopKnowledgeBuilder {

    public ShopKnowledgeDocument build(ShopDTO shop, List<VoucherDTO> vouchers, float[] embedding) {
        List<String> tags = new ArrayList<>();
        if (shop.getArea() != null) {
            tags.add(shop.getArea());
        }
        if (shop.getOpenHours() != null) {
            tags.add(shop.getOpenHours());
        }
        if (shop.getAvgPrice() != null) {
            tags.add("人均" + shop.getAvgPrice());
        }
        if (shop.getScore() != null) {
            tags.add("评分" + (shop.getScore() / 10.0));
        }

        String voucherDesc = vouchers == null || vouchers.isEmpty()
                ? "暂无可用优惠券信息"
                : vouchers.stream()
                .map(v -> String.format("券名:%s, 原价:%s, 现价:%s, 规则:%s",
                        safe(v.getTitle()),
                        safe(v.getActualValue()),
                        safe(v.getPayValue()),
                        safe(v.getRules())))
                .collect(Collectors.joining("；"));

        String content = "商家名称：" + safe(shop.getName()) + "\n"
                + "商家ID：" + safe(shop.getId()) + "\n"
                + "商圈：" + safe(shop.getArea()) + "\n"
                + "地址：" + safe(shop.getAddress()) + "\n"
                + "营业时间：" + safe(shop.getOpenHours()) + "\n"
                + "人均价格：" + safe(shop.getAvgPrice()) + "\n"
                + "评分：" + (shop.getScore() == null ? "未知" : shop.getScore() / 10.0) + "\n"
                + "销量：" + safe(shop.getSold()) + "\n"
                + "评论数：" + safe(shop.getComments()) + "\n"
                + "优惠券：" + voucherDesc;

        return ShopKnowledgeDocument.builder()
                .id(String.valueOf(shop.getId()))
                .shopId(shop.getId())
                .name(shop.getName())
                .typeId(shop.getTypeId())
                .area(shop.getArea())
                .address(shop.getAddress())
                .openHours(shop.getOpenHours())
                .avgPrice(shop.getAvgPrice())
                .score(shop.getScore())
                .sold(shop.getSold())
                .comments(shop.getComments())
                .tags(tags.stream().filter(Objects::nonNull).distinct().toList())
                .content(content)
                .contentVector(embedding)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String safe(Object value) {
        return value == null ? "未知" : String.valueOf(value);
    }
}
