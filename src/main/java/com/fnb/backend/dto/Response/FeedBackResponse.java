package com.fnb.backend.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FeedBackResponse {
    private Long id;
    private String content;
    private String image;
    private float star;
    private java.time.LocalDateTime createTime;
}
