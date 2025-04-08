package com.example.guard.demos.web.controller;

import com.example.guard.demos.web.service.ExamService;
import com.example.guard.demos.web.service.TeacherService;
import com.example.guard.demos.web.entity.Exam;
import com.example.guard.demos.web.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ExamService examService;

    @GetMapping("/teacher/login")
    public String showLoginForm() {
        return "teacher-login";
    }

    @PostMapping("/teacher/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes redirectAttributes) {
        Teacher teacher = teacherService.findByUsername(username);
        if (teacher != null && teacher.getPassword().equals(password)) {
            session.setAttribute("teacher", teacher);
            List<Exam> exams = examService.findByTeacherId(teacher.getId());
            redirectAttributes.addFlashAttribute("teacher", teacher);
            redirectAttributes.addFlashAttribute("exams", exams);
            return "redirect:/teacher/exam-info/" + teacher.getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/teacher/login";
        }
    }

    @GetMapping("/teacher/logout")
    public String logout() {
        // 实现登出逻辑
        return "redirect:/teacher/login";
    }

    @GetMapping("/teacher/info")
    public String showTeacherInfo(@RequestParam Long id, Model model) {
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("teacher", teacher);
        return "teacher-info";
    }

    @PostMapping("/teacher/update")
    public String updateTeacher(@RequestParam Long id, @RequestParam String username, @RequestParam String password, @RequestParam String name, @RequestParam String gender, @RequestParam String phone, @RequestParam String subject, RedirectAttributes redirectAttributes) {
        Teacher teacher = teacherService.findById(id);
        if (teacher != null) {
            teacher.setUsername(username);
            teacher.setPassword(password);
            teacher.setName(name);
            teacher.setGender(gender);
            teacher.setPhone(phone);
            teacher.setSubject(subject);
            teacherService.save(teacher);
            redirectAttributes.addFlashAttribute("success", "教师信息更新成功");
        } else {
            redirectAttributes.addFlashAttribute("error", "教师信息更新失败");
        }
        return "redirect:/teacher/info?id=" + id;
    }

    @GetMapping("/teacher/exam-info/{id}")
    public String showExamInfo(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.findById(id);
        List<Exam> exams = examService.findByTeacherIdInExamTeacherIds(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("exams", exams);
        return "teacher-exam-info";
    }

    @GetMapping("/teacher/report-details")
    public String showReportDetails(@RequestParam Long examId, Model model) {
        model.addAttribute("examId", examId);
        return "report-details";
    }

    @PostMapping("/teacher/report-student")
    public String reportStudent(@RequestParam Long examId,
                                @RequestParam String reportedStudentUsername,
                                @RequestParam String reportDescription,
                                @RequestParam("report_image1") MultipartFile reportImage1,
                                @RequestParam("report_image2") MultipartFile reportImage2,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        System.out.println("Received examId: " + examId);
        System.out.println("Received reportedStudentUsername: " + reportedStudentUsername);
        System.out.println("Received reportDescription: " + reportDescription);
        System.out.println("Received reportImage1: " + reportImage1.getOriginalFilename());
        System.out.println("Received reportImage2: " + reportImage2.getOriginalFilename());

        Exam exam = examService.findById(examId);
        if (exam != null) {
            System.out.println("Exam found with ID: " + exam.getId());
            System.out.println("Teacher ID associated with exam: " + exam.getTeacherId());

            exam.setReportedStudentUsername(reportedStudentUsername);
            exam.setReportDescription(reportDescription);

            // 保存上传的图片到 D:/uploads/ 目录
            String uploadDir = "D:/uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            try {
                if (!reportImage1.isEmpty()) {
                    String fileName1 = reportImage1.getOriginalFilename();
                    reportImage1.transferTo(new File(uploadPath, fileName1));
                    exam.setReportImage1("/uploads/" + fileName1);
                }
                if (!reportImage2.isEmpty()) {
                    String fileName2 = reportImage2.getOriginalFilename();
                    reportImage2.transferTo(new File(uploadPath, fileName2));
                    exam.setReportImage2("/uploads/" + fileName2);
                }
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "文件上传失败");
                return "redirect:/teacher/exam-info/" + exam.getTeacherId();
            }
            examService.save(exam);
            redirectAttributes.addFlashAttribute("success", "举报信息已提交");

            // 从 session 中获取当前教师的信息
            Teacher teacher = (Teacher) session.getAttribute("teacher");
            if (teacher != null) {
                return "redirect:/teacher/exam-info/" + teacher.getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "教师信息未找到");
                return "redirect:/teacher/exam-info/";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "考试信息未找到");
            return "redirect:/teacher/exam-info/";
        }
    }

}
