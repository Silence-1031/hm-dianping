package com.hmdp.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrievalShop {
    private Long shopId;
    private String name;
    private String area;
    private String address;
    private String openHours;
    private Long avgPrice;
    private Integer score;
    private Double retrieveScore;
    private String content;
}
