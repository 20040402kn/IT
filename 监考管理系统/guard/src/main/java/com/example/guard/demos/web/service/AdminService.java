package com.example.guard.demos.web.service;

import com.example.guard.demos.web.entity.Admin;
import com.example.guard.demos.web.entity.AdminPermission;
import com.example.guard.demos.web.entity.AdminPermissionId;
import com.example.guard.demos.web.repository.AdminPermissionRepository;
import com.example.guard.demos.web.repository.AdminRepository;
import com.example.guard.demos.web.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminPermissionRepository adminPermissionRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    public void addPermission(Long adminId, Long permissionId) {
        AdminPermission adminPermission = new AdminPermission();
        AdminPermissionId id = new AdminPermissionId();
        id.setAdminId(adminId);
        id.setPermissionId(permissionId);
        adminPermission.setId(id);
        adminPermissionRepository.save(adminPermission);
    }






}
