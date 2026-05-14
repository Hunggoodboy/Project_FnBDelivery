package com.fnb.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterDTO {
    private String username, phone_number, password,confirmPassword, email;

}
