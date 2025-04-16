package com.example.guard.demos.web.service;

import com.example.guard.demos.web.entity.Teacher;
import com.example.guard.demos.web.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }

    public void batchDeleteByIds(List<Long> ids) {
        teacherRepository.deleteAllById(ids);
    }

    public List<Teacher> searchTeachers(String searchTerm) {
        try {
            Long id = Long.parseLong(searchTerm);
            return teacherRepository.findByIdOrNameOrUsername(id, searchTerm);
        } catch (NumberFormatException e) {
            return teacherRepository.findByIdOrNameOrUsername(null, searchTerm);
        }
    }
    public Teacher findByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    public Teacher findById(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Page<Teacher> findAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }
}
