package com.example.guard.demos.web.service;

import com.example.guard.demos.web.repository.PermissionRepository;
import com.example.guard.demos.web.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public Permission findByName(String name) {
        return permissionRepository.findByName(name);
    }
}
