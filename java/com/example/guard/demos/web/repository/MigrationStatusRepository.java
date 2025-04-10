package com.example.guard.demos.web.repository;

import com.example.guard.demos.web.entity.MigrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MigrationStatusRepository extends JpaRepository<MigrationStatus, Long> {
    MigrationStatus findByMigrationName(String migrationName);
}
