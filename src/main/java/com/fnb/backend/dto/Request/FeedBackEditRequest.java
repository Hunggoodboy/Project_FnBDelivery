package com.fnb.backend.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedBackEditRequest {
    private String content;
    private String image;
    private float star;
    private Long feedBackId;
}
