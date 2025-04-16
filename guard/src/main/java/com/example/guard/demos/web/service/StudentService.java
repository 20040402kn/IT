package com.example.guard.demos.web.service;


import com.example.guard.demos.web.entity.Exam;
import com.example.guard.demos.web.entity.StudentExam;
import com.example.guard.demos.web.repository.ExamRepository;
import com.example.guard.demos.web.repository.StudentExamRepository;
import com.example.guard.demos.web.repository.StudentRepository;
import com.example.guard.demos.web.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentExamRepository studentExamRepository;

    @Autowired
    private ExamRepository examRepository;

    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username);
    }

    public List<StudentExam> findStudentExamsByStudentId(Long studentId) {
        return studentExamRepository.findByStudentId(studentId);
    }

    public void updateStudent(Student student) {
        studentRepository.save(student);
    }


    public List<Exam> findExamsReportedByStudentUsername(String reportedStudentUsername) {
        return examRepository.findExamsWithReportsByReportedStudentUsername(reportedStudentUsername);
    }
}
