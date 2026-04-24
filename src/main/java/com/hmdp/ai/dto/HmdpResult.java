package com.hmdp.ai.dto;

import lombok.Data;

@Data
public class HmdpResult<T> {
    private Boolean success;
    private String errorMsg;
    private T data;
    private Long total;
}