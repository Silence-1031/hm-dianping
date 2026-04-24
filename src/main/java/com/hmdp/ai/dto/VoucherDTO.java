package com.hmdp.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VoucherDTO {
    private Long id;
    private Long shopId;
    private String title;
    private String subTitle;
    private String rules;
    private Long payValue;
    private Long actualValue;
    private Integer type;
    private Integer stock;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}