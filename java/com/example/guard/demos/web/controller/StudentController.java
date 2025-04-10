package com.example.guard.demos.web.controller;

import com.example.guard.demos.web.service.ExamService;
import com.example.guard.demos.web.service.StudentService;
import com.example.guard.demos.web.entity.Exam;
import com.example.guard.demos.web.entity.Student;
import com.example.guard.demos.web.entity.StudentExam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamService examService;

    @GetMapping("/student/login")
    public String showLoginForm() {
        return "student-login";
    }

    @PostMapping("/student/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        Student student = studentService.findByUsername(username);
        if (student != null && student.getPassword().equals(password)) {
            session.setAttribute("student", student);
            List<StudentExam> studentExams = studentService.findStudentExamsByStudentId(student.getId());
            List<Exam> examsReportedByStudent = examService.findExamsReportedByStudent(student.getUsername());
            List<Exam> reportedExams = studentService.findExamsReportedByStudentUsername(student.getUsername());
            model.addAttribute("student", student);
            model.addAttribute("studentExams", studentExams);
            model.addAttribute("examsReportedByStudent", examsReportedByStudent);
            model.addAttribute("reportedExams", reportedExams);
            return "student-info";
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "student-login";
        }
    }

    @GetMapping("/student/info")
    public String showStudentInfo(@RequestParam String username, Model model, HttpSession session) {
        Student student = studentService.findByUsername(username);
        if (student != null) {
            List<StudentExam> studentExams = studentService.findStudentExamsByStudentId(student.getId());
            List<Exam> examsReportedByStudent = examService.findExamsReportedByStudentAndStudentExam(student.getUsername());
            // 打印日志或调试信息，确保 examsReportedByStudent 不为空
            System.out.println("Exams reported by student: " + examsReportedByStudent.size());
            model.addAttribute("student", student);
            model.addAttribute("studentExams", studentExams);
            model.addAttribute("examsReportedByStudent", examsReportedByStudent);
            return "student-info";
        } else {
            model.addAttribute("error", "用户未找到");
            return "student-login";
        }
    }

    @PostMapping("/student/updateInfo")
    public String updateStudentInfo(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam("name") String name,
                                    @RequestParam("gender") String gender,
                                    @RequestParam("age") int age,
                                    @RequestParam("major") String major,
                                    @RequestParam("phone") String phone,
                                    Model model, HttpSession session) {
        Student student = studentService.findByUsername(username);
        if (student != null) {
            student.setPassword(password);
            student.setName(name);
            student.setGender(gender);
            student.setAge(age);
            student.setMajor(major);
            student.setPhone(phone);
            studentService.updateStudent(student);

            // 获取学生考试信息
            List<StudentExam> studentExams = studentService.findStudentExamsByStudentId(student.getId());
            List<Exam> examsReportedByStudent = examService.findExamsReportedByStudentAndStudentExam(student.getUsername());
            model.addAttribute("student", student);
            model.addAttribute("studentExams", studentExams);
            model.addAttribute("examsReportedByStudent", examsReportedByStudent);
            model.addAttribute("success", "信息更新成功");
            return "student-info";
        } else {
            model.addAttribute("error", "用户未找到");
            return "student-login";
        }
    }

    @GetMapping("/student/reports")
    public String showStudentReports(HttpSession session, Model model) {
        Student student = (Student) session.getAttribute("student");
        if (student != null) {
            String reportedStudentUsername = student.getUsername();
            List<Exam> reportedExams = examService.findExamsWithReportsByReportedStudentUsername(reportedStudentUsername);
            model.addAttribute("reportedExams", reportedExams);
            return "student-reports";
        } else {
            model.addAttribute("error", "用户未找到");
            return "student-login";
        }
    }

}
