package com.hmdp.ai.service;

import com.hmdp.ai.client.HmDianPingHttpClient;
import com.hmdp.ai.document.ShopKnowledgeDocument;
import com.hmdp.ai.dto.ReindexResponse;
import com.hmdp.ai.dto.ShopDTO;
import com.hmdp.ai.dto.VoucherDTO;
import com.hmdp.ai.repository.ShopKnowledgeRepository;
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
public class ShopSyncService {

    private final HmDianPingHttpClient hmDianPingHttpClient;
    private final ShopKnowledgeBuilder shopKnowledgeBuilder;
    private final ShopKnowledgeRepository shopKnowledgeRepository;
    private final EmbeddingModel embeddingModel;

    @Value("${hmdp.sync.max-pages-per-type:20}")
    private Integer maxPagesPerType;

    @Value("${hmdp.sync.supported-type-ids}")
    private List<Integer> supportedTypeIds;

    public ReindexResponse reindexAll() {
        int successCount = 0;
        int failCount = 0;

        for (Integer typeId : supportedTypeIds) {
            for (int current = 1; current <= maxPagesPerType; current++) {
                List<ShopDTO> shops = hmDianPingHttpClient.queryShopsByType(typeId, current);
                if (shops == null || shops.isEmpty()) {
                    break;
                }
                for (ShopDTO briefShop : shops) {
                    try {
                        ShopDTO shop = hmDianPingHttpClient.queryShopById(briefShop.getId());
                        if (shop == null) {
                            failCount++;
                            continue;
                        }
                        List<VoucherDTO> vouchers = hmDianPingHttpClient.queryVouchersByShopId(shop.getId());
                        float[] embedding = embeddingModel.embed(shopText(shop, vouchers));
                        ShopKnowledgeDocument document = shopKnowledgeBuilder.build(shop, vouchers, embedding);
                        shopKnowledgeRepository.save(document);
                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                        log.error("reindex shop failed, shopId={}", briefShop.getId(), e);
                    }
                }
            }
        }

        return ReindexResponse.builder()
                .successCount(successCount)
                .failCount(failCount)
                .build();
    }

    private String shopText(ShopDTO shop, List<VoucherDTO> vouchers) {
        StringBuilder sb = new StringBuilder();
        sb.append(shop.getName()).append(" ")
                .append(nullSafe(shop.getArea())).append(" ")
                .append(nullSafe(shop.getAddress())).append(" ")
                .append(nullSafe(shop.getOpenHours())).append(" ")
                .append(nullSafe(shop.getAvgPrice())).append(" ")
                .append(nullSafe(shop.getScore())).append(" ");

        if (vouchers != null) {
            for (VoucherDTO voucher : vouchers) {
                sb.append(nullSafe(voucher.getTitle())).append(" ")
                        .append(nullSafe(voucher.getSubTitle())).append(" ")
                        .append(nullSafe(voucher.getRules())).append(" ");
            }
        }
        return sb.toString();
    }


    private String nullSafe(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }
}
