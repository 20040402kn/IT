package com.example.guard.demos.web.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String time;
    private String location;
    private String end_time;

    private Long teacherId; // 外键关联教师

    @ManyToMany
    @JoinTable(
            name = "exam_teacher",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private List<Teacher> teachers;

    @OneToMany(mappedBy = "exam")
    private List<StudentExam> studentExams;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ExamRoom room;

    @OneToMany(mappedBy = "exam")
    private List<Report> reports;

    @ElementCollection
    @CollectionTable(name = "exam_teacher_ids", joinColumns = @JoinColumn(name = "exam_id"))
    @Column(name = "teacher_id")
    private List<Long> teacherIds;

    @Column(name = "reported_student_username")
    private String reportedStudentUsername;

    @Column(name = "report_description")
    private String reportDescription;

    @Column(name = "report_image1")
    private String reportImage1;

    @Column(name = "report_image2")
    private String reportImage2;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ExamRoom getRoom() {
        return room;
    }

    public void setRoom(ExamRoom room) {
        this.room = room;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public List<StudentExam> getStudentExams() {
        return studentExams;
    }

    public void setStudentExams(List<StudentExam> studentExams) {
        this.studentExams = studentExams;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public List<Long> getTeacherIds() {
        return teacherIds;
    }

    public void setTeacherIds(List<Long> teacherIds) {
        this.teacherIds = teacherIds;
    }

    public String getReportedStudentUsername() {
        return reportedStudentUsername;
    }

    public void setReportedStudentUsername(String reportedStudentUsername) {
        this.reportedStudentUsername = reportedStudentUsername;
    }

    public String getReportDescription() {
        return reportDescription;
    }

    public void setReportDescription(String reportDescription) {
        this.reportDescription = reportDescription;
    }

    public String getReportImage1() {
        return reportImage1;
    }

    public void setReportImage1(String reportImage1) {
        this.reportImage1 = reportImage1;
    }

    public String getReportImage2() {
        return reportImage2;
    }

    public void setReportImage2(String reportImage2) {
        this.reportImage2 = reportImage2;
    }
}
