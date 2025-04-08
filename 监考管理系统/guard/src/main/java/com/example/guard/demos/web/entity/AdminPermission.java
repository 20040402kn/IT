package com.example.guard.demos.web.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class AdminPermission {
    @EmbeddedId
    private AdminPermissionId id;

    // Getters and Setters
    public AdminPermissionId getId() {
        return id;
    }

    public void setId(AdminPermissionId id) {
        this.id = id;
    }
}
