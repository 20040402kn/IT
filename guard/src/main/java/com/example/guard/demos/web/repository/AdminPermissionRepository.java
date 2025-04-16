package com.example.guard.demos.web.repository;

import com.example.guard.demos.web.entity.AdminPermission;
import com.example.guard.demos.web.entity.AdminPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminPermissionRepository extends JpaRepository<AdminPermission, AdminPermissionId> {
    List<AdminPermission> findById_AdminId(Long adminId);
}
