package com.fnb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSessionDTO {
    private Long id;
    private String user_name, role;
}
