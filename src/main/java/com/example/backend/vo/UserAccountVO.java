package com.example.backend.vo;

import com.example.backend.entity.UserAccount;

public class UserAccountVO extends UserAccount {
    // 可扩展展示字段
    private String role;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}


