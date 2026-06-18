package com.nexus.realestate.dto;

import com.nexus.realestate.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private UserRole role;
}