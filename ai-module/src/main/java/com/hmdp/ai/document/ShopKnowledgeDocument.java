package com.hmdp.ai.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "shop_knowledge")
public class ShopKnowledgeDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long shopId;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Long)
    private Long typeId;

    @Field(type = FieldType.Keyword)
    private String area;

    @Field(type = FieldType.Text)
    private String address;

    @Field(type = FieldType.Keyword)
    private String openHours;

    @Field(type = FieldType.Long)
    private Long avgPrice;

    @Field(type = FieldType.Integer)
    private Integer score;

    @Field(type = FieldType.Integer)
    private Integer sold;

    @Field(type = FieldType.Integer)
    private Integer comments;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Auto)
    private float[] contentVector;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;
}
