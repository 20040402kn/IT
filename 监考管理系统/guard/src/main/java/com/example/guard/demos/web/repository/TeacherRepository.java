package com.example.guard.demos.web.repository;

import com.example.guard.demos.web.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Teacher findByUsername(String username);
    void deleteById(Long id);
    @Query("SELECT t FROM Teacher t WHERE t.name LIKE %:searchTerm% OR t.username LIKE %:searchTerm%")
    List<Teacher> searchTeachers(@Param("searchTerm") String searchTerm);

    @Query("SELECT t FROM Teacher t WHERE t.id = :id OR t.name LIKE %:searchTerm% OR t.username LIKE %:searchTerm%")
    List<Teacher> findByIdOrNameOrUsername(@Param("id") Long id, @Param("searchTerm") String searchTerm);
}
