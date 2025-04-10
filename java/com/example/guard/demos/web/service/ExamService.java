package com.example.guard.demos.web.service;

import com.example.guard.demos.web.repository.ExamRepository;
import com.example.guard.demos.web.entity.*;
import com.example.guard.demos.web.repository.ExamRoomRepository;
import com.example.guard.demos.web.repository.StudentExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {
    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamRoomRepository examRoomRepository;

    @Autowired
    private StudentExamRepository studentExamRepository;


    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam findById(Long id) {
        return examRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        examRepository.deleteById(id);
    }

    // 修改 batchDeleteByIds 方法以适应 Spring Data JPA 的批量删除操作
    public void batchDeleteByIds(List<Long> ids) {
        ids.forEach(id -> examRepository.deleteById(id));
    }

    public List<Exam> searchExams(String searchTerm) {
        return examRepository.searchExams(searchTerm);
    }


    public List<Exam> findByTeacherId(Long teacherId) {
        return examRepository.findByTeacherId(teacherId);
    }

    public Page<Exam> findAllExams(Pageable pageable) {
        return examRepository.findAll(pageable);
    }

    public List<Exam> findTop8Exams() {
        return examRepository.findTop8ByTimeAsc().stream().limit(8).collect(Collectors.toList());
    }

    public List<Exam> findBySubjectOrIdUsingQuery(String subject, Long id) {
        return examRepository.findBySubjectOrId(subject, id);
    }

    public List<Exam> getExamsByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Exam> examPage = examRepository.findAll(pageable);
        return examPage.getContent();
    }

    public long countAllExams() {
        return examRepository.count();
    }

    public List<Exam> findByTeacherIds(List<Long> teacherIds) {
        return examRepository.findByTeacherIdsIn(teacherIds);
    }

    public List<Exam> findByTeacherIdInExamTeacherIds(Long teacherId) {
        return examRepository.findByTeacherIdInExamTeacherIds(teacherId);
    }

    public void migrateExamLocationsToExamRooms() {
        List<Exam> exams = examRepository.findAll();
        for (Exam exam : exams) {
            String location = exam.getLocation();
            // 假设 location 格式为 "楼号-教室号"
            String[] parts = location.split("-");
            if (parts.length == 2) {
                String building = parts[0];
                String roomNumber = parts[1];

                ExamRoom examRoom = new ExamRoom();
                examRoom.setBuilding(building);
                examRoom.setRoom_number(roomNumber);
                // 假设每个教室的容量为 50，你可以根据实际情况调整
                examRoom.setCapacity(50);

                ExamRoom savedExamRoom = examRoomRepository.save(examRoom);
                exam.setRoom(savedExamRoom);
                examRepository.save(exam);
            }
        }
    }

    public List<Exam> findExamsWithReports() {
        return examRepository.findExamsWithReports();
    }


    public List<Exam> findExamsWithNonEmptyReportedStudentUsername() {
        return examRepository.findExamsWithNonEmptyReportedStudentUsername();
    }

    public List<Exam> findExamsReportedByStudent(String reportedStudentUsername) {
        return examRepository.findExamsWithReportsByStudentUsername(reportedStudentUsername);
    }
    public List<Exam> findExamsWithReportsByReportedStudentUsername(String username) {
        return examRepository.findExamsWithReportsByReportedStudentUsername(username);
    }

    public List<Exam> findExamsReportedByStudentAndStudentExam(String studentUsername) {
        List<StudentExam> studentExams = studentExamRepository.findByStudentUsername(studentUsername);
        List<Long> examIds = studentExams.stream()
                .map(StudentExam::getExam)
                .map(Exam::getId)
                .collect(Collectors.toList());
        return examRepository.findByIdInAndReportedStudentUsername(examIds, studentUsername);
    }
}
