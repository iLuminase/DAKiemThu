package com.inventory.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String clerkUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
