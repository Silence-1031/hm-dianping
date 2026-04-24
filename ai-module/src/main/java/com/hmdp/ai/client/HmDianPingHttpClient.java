package com.hmdp.ai.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.ai.dto.HmdpResult;
import com.hmdp.ai.dto.ShopDTO;
import com.hmdp.ai.dto.VoucherDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HmDianPingHttpClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${hmdp.base-url}")
    private String hmdpBaseUrl;

    public List<ShopDTO> queryShopsByType(Integer typeId, Integer current) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(hmdpBaseUrl + "/shop/of/type");

        if (typeId != null) {
            builder.queryParam("typeId", typeId);
        }
        if (current != null) {
            builder.queryParam("current", current);
        }

        String url = builder.toUriString();
        log.info("queryShopsByType url={}", url);
        try {
            ResponseEntity<HmdpResult<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<HmdpResult<Object>>() {}
            );
            HmdpResult<Object> body = response.getBody();
            if (body == null || !Boolean.TRUE.equals(body.getSuccess()) || body.getData() == null) {
                return Collections.emptyList();
            }
            return objectMapper.convertValue(body.getData(), new TypeReference<List<ShopDTO>>() {});
        } catch (Exception e) {
            log.error("queryShopsByType failed, typeId={}, current={}", typeId, current, e);
            return Collections.emptyList();
        }
    }

    public ShopDTO queryShopById(Long shopId) {
        if (shopId == null) {
            log.warn("queryShopById skipped, shopId is null");
            return null;
        }
        String url = hmdpBaseUrl + "/shop/" + shopId;
        log.info("queryShopById url={}", url);
        try {
            ResponseEntity<HmdpResult<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<HmdpResult<Object>>() {}
            );
            HmdpResult<Object> body = response.getBody();
            if (body == null || !Boolean.TRUE.equals(body.getSuccess()) || body.getData() == null) {
                return null;
            }
            return objectMapper.convertValue(body.getData(), ShopDTO.class);
        } catch (Exception e) {
            log.error("queryShopById failed, shopId={}", shopId, e);
            return null;
        }
    }

    public List<VoucherDTO> queryVouchersByShopId(Long shopId) {
        if (shopId == null) {
            log.warn("queryVouchersByShopId skipped, shopId is null");
            return Collections.emptyList();
        }
        String url = hmdpBaseUrl + "/voucher/list/" + shopId;
        log.info("queryVouchersByShopId url={}", url);
        try {
            ResponseEntity<HmdpResult<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<HmdpResult<Object>>() {}
            );
            HmdpResult<Object> body = response.getBody();
            if (body == null || !Boolean.TRUE.equals(body.getSuccess()) || body.getData() == null) {
                return Collections.emptyList();
            }
            return objectMapper.convertValue(body.getData(), new TypeReference<List<VoucherDTO>>() {});
        } catch (Exception e) {
            log.error("queryVouchersByShopId failed, shopId={}", shopId, e);
            return Collections.emptyList();
        }
    }
}