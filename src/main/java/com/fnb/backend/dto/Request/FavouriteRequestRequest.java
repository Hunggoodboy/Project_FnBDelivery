package com.fnb.backend.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FavouriteRequestRequest {
    private String product;
    private String note;
    private String type;
}
