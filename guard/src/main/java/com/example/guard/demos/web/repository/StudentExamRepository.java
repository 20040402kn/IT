package com.example.guard.demos.web.repository;


import com.example.guard.demos.web.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    List<StudentExam> findByStudentId(Long studentId);

    List<StudentExam> findByStudentUsername(String studentUsername);

}

