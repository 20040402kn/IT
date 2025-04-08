package com.example.guard.demos.web.controller;

import com.example.guard.demos.web.entity.*;
import com.example.guard.demos.web.repository.ExamRoomRepository;
import com.example.guard.demos.web.service.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private ExamService examService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private ExamRoomRepository examRoomRepository;

    @Autowired
    private ExamRoomService examRoomService;


    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin-login";
    }

    @PostMapping("/admin/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password,
                                     HttpSession session, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to login with username: {}", username);
        logger.info("Attempting to login with password: {}", password);
        try {
            Admin admin = adminService.findByUsername(username);
            if (admin != null && admin.getPassword().equals(password)) {
                session.setAttribute("admin", admin); // 确保 Admin 类已正确导入
                logger.info("Login successful for username: {}", username);
                logger.info("Admin session attribute set: {}", session.getAttribute("admin"));
                return Map.of("success", true, "message", "登录成功", "redirectUrl", "/admin/dashboard");
            } else {
                logger.error("Login failed for username: {}", username);
                return Map.of("success", false, "message", "Invalid username or password");
            }
        } catch (Exception e) {
            logger.error("Error during login: ", e);
            return Map.of("success", false, "message", "系统错误，请稍后再试");
        }
    }


    @GetMapping("/admin/dashboard")
    public String showDashboard(Model model) {
        List<Exam> recentExams = examService.findTop8Exams(); // 使用新的查询方法
        model.addAttribute("recentExams", recentExams);
        return "admin-dashboard";
    }

    @PostMapping("/admin/import-exam-info")
    public String importExamInfo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "请选择一个文件");
            return "redirect:/admin/import-exam-info";
        }

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) { // Skip header row
                    continue;
                }
                Cell subjectCell = row.getCell(0);
                Cell timeCell = row.getCell(1);
                Cell end_timeCell = row.getCell(2);
                Cell locationCell = row.getCell(3);
                Cell teacherIdsCell = row.getCell(4);

                if (subjectCell != null && timeCell != null && locationCell != null && teacherIdsCell != null) {
                    Exam exam = new Exam();

                    // 获取 subjectCell 的值
                    if (subjectCell.getCellType() == CellType.STRING) {
                        exam.setSubject(subjectCell.getStringCellValue());
                    } else {
                        exam.setSubject(String.valueOf(subjectCell.getNumericCellValue()));
                    }

                    // 获取 timeCell 的值
                    if (timeCell.getCellType() == CellType.STRING) {
                        exam.setTime(timeCell.getStringCellValue());
                    } else {
                        exam.setTime(String.valueOf(timeCell.getNumericCellValue()));
                    }

                    // 获取 end_timeCell 的值
                    if (end_timeCell.getCellType() == CellType.STRING) {
                        exam.setEnd_time(end_timeCell.getStringCellValue());
                    } else {
                        exam.setEnd_time(String.valueOf(end_timeCell.getNumericCellValue()));
                    }

                    // 获取 locationCell 的值
                    if (locationCell.getCellType() == CellType.STRING) {
                        exam.setLocation(locationCell.getStringCellValue());
                    } else {
                        exam.setLocation(String.valueOf(locationCell.getNumericCellValue()));
                    }

                    // 获取 teacherIdsCell 的值
                    if (teacherIdsCell != null) {
                        if (teacherIdsCell.getCellType() == CellType.STRING) {
                            String[] teacherIdStrings = teacherIdsCell.getStringCellValue().split(",");
                            List<Long> teacherIds = new ArrayList<>();
                            for (String idStr : teacherIdStrings) {
                                teacherIds.add(Long.parseLong(idStr.trim()));
                            }
                            exam.setTeacherIds(teacherIds);
                        } else if (teacherIdsCell.getCellType() == CellType.NUMERIC) {
                            List<Long> teacherIds = new ArrayList<>();
                            teacherIds.add((long) teacherIdsCell.getNumericCellValue());
                            exam.setTeacherIds(teacherIds);
                        }
                    }

                    examService.save(exam);
                }

            }
            redirectAttributes.addFlashAttribute("success", "监考信息导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "文件读取或保存失败");
        }

        return "redirect:/admin/import-exam-info";
    }


    @GetMapping("/admin/import-exam-info")
    public String showImportExamInfoForm() {
        return "admin-import-exam-info";
    }

    @GetMapping("/admin/manage-exam-info")
    public String manageExamInfo(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Exam> exams = examService.findAllExams(pageable);
        model.addAttribute("exams", exams.getContent());
        model.addAttribute("currentPage", exams.getNumber());
        model.addAttribute("totalPages", exams.getTotalPages());
        return "admin-manage-exam-info";
    }


    @GetMapping("/admin/edit-exam-info/{id}")
    public String showEditExamInfoForm(@PathVariable Long id, Model model) {
        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);
        return "admin-edit-exam-info";
    }

    @PostMapping("/admin/edit-exam-info/{id}")
    @ResponseBody
    public Map<String, Object> editExamInfo(@PathVariable Long id, @ModelAttribute Exam exam) {
        exam.setId(id);
        // 解析 teacherIds 列表并转换为逗号分隔的字符串
        String teacherIdsStr = exam.getTeacherIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        // 将字符串重新分割为 List<Long>
        String[] teacherIdArray = teacherIdsStr.split(",");
        List<Long> teacherIds = new ArrayList<>();
        for (String idStr : teacherIdArray) {
            teacherIds.add(Long.parseLong(idStr.trim()));
        }
        exam.setTeacherIds(teacherIds);
        Map<String, Object> response = new HashMap<>();
        try {
            examService.save(exam);
            response.put("success", true);
            response.put("message", "考试信息更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "保存失败，请重试");
        }
        return response;
    }

    @GetMapping("/admin/delete-exam-info/{id}")
    public String deleteExamInfo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        examService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "考试信息删除成功");
        return "redirect:/admin/manage-exam-info";
    }

    @GetMapping("/admin/add-exam-info")
    public String showAddExamInfoForm(Model model) {
        model.addAttribute("exam", new Exam());
        return "admin-add-exam-info";
    }

    @PostMapping("/admin/add-exam-info")
    public String addExamInfo(@ModelAttribute Exam exam, RedirectAttributes redirectAttributes) {
        // 解析 teacherIds 字符串
        String teacherIdsStr = exam.getTeacherIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String[] teacherIdArray = teacherIdsStr.split(",");
        List<Long> teacherIds = new ArrayList<>();
        for (String idStr : teacherIdArray) {
            teacherIds.add(Long.parseLong(idStr.trim()));
        }
        exam.setTeacherIds(teacherIds);

        examService.save(exam);
        redirectAttributes.addFlashAttribute("success", "考试信息添加成功");
        return "redirect:/admin/manage-exam-info";
    }

    @GetMapping("/admin/search-exams")
    public String searchExams(@RequestParam String searchTerm, Model model) {
        List<Long> teacherIds = new ArrayList<>();
        if (searchTerm.contains(",")) {
            String[] ids = searchTerm.split(",");
            for (String id : ids) {
                teacherIds.add(Long.parseLong(id.trim()));
            }
            List<Exam> exams = examService.findByTeacherIds(teacherIds);
            model.addAttribute("exams", exams);
        } else {
            List<Exam> exams = examService.searchExams(searchTerm);
            model.addAttribute("exams", exams);
        }
        return "admin-manage-exam-info";
    }


    @PostMapping("/admin/batch-delete-exam-info")
    public String batchDeleteExamInfo(@RequestParam List<Long> examIds, RedirectAttributes redirectAttributes) {
        try {
            examService.batchDeleteByIds(examIds);
            redirectAttributes.addFlashAttribute("success", "考试信息批量删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "考试信息批量删除失败");
        }
        return "redirect:/admin/manage-exam-info";
    }

    @GetMapping("/admin/manage-teachers")
    public String manageTeachers(@RequestParam(defaultValue = "0") int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Teacher> teachers = teacherService.findAllTeachers(pageable);
        model.addAttribute("teachers", teachers.getContent());
        model.addAttribute("currentPage", page); // 确保 currentPage 总是有值
        model.addAttribute("totalPages", teachers.getTotalPages());
        return "admin-manage-teachers";
    }


    @GetMapping("/admin/add-teacher")
    public String showAddTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "admin-add-teacher";
    }

    @PostMapping("/admin/add-teacher")
    public String addTeacher(@ModelAttribute Teacher teacher, RedirectAttributes redirectAttributes) {
        teacherService.save(teacher);
        redirectAttributes.addFlashAttribute("success", "教师信息添加成功");
        return "redirect:/admin/manage-teachers";
    }

    @GetMapping("/admin/edit-teacher/{id}")
    public String showEditTeacherForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.findById(id);
        model.addAttribute("teacher", teacher);
        return "admin-edit-teacher";
    }

    @PostMapping("/admin/edit-teacher/{id}")
    public String editTeacher(@PathVariable Long id, @ModelAttribute Teacher teacher, RedirectAttributes redirectAttributes) {
        teacher.setId(id);
        teacherService.save(teacher);
        redirectAttributes.addFlashAttribute("success", "教师信息更新成功");
        return "redirect:/admin/manage-teachers";
    }

    @GetMapping("/admin/delete-teacher/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        teacherService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "教师信息删除成功");
        return "redirect:/admin/manage-teachers";
    }

    @GetMapping("/admin/search-teachers")
    public String searchTeachers(@RequestParam String searchTerm, Model model) {
        List<Teacher> teachers = teacherService.searchTeachers(searchTerm);
        model.addAttribute("teachers", teachers);
        return "admin-manage-teachers";
    }

    @PostMapping("/admin/batch-delete-teachers")
    public String batchDeleteTeachers(@RequestParam List<Long> teacherIds, RedirectAttributes redirectAttributes) {
        try {
            teacherService.batchDeleteByIds(teacherIds);
            redirectAttributes.addFlashAttribute("success", "教师信息批量删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "教师信息批量删除失败");
        }
        return "redirect:/admin/manage-teachers";
    }

    @GetMapping("/admin/logout")
    public String logout() {
        // 实现登出逻辑
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/exam-announcements")
    public String showExamAnnouncements(Model model) {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        model.addAttribute("announcements", announcements);
        return "admin-exam-announcements";
    }


    @GetMapping("/admin/add-exam-announcement")
    public String showAddExamAnnouncementForm(Model model) {
        model.addAttribute("announcement", new Announcement());
        return "admin-add-exam-announcement";
    }

    @PostMapping("/admin/add-exam-announcement")
    public String addExamAnnouncement(@ModelAttribute("announcement") Announcement announcement, RedirectAttributes redirectAttributes) {
        announcementService.save(announcement);
        redirectAttributes.addFlashAttribute("success", "公告添加成功");
        return "redirect:/admin/exam-announcements";
    }

    @GetMapping("/admin/edit-exam-announcement/{id}")
    public String showEditExamAnnouncementForm(@PathVariable Long id, Model model) {
        Optional<Announcement> announcement = announcementService.findById(id);
        if (announcement.isPresent()) {
            model.addAttribute("announcement", announcement.get());
        } else {
            // 处理公告不存在的情况
            return "redirect:/admin/exam-announcements";
        }
        return "admin-edit-exam-announcement";
    }

    @PostMapping("/admin/edit-exam-announcement/{id}")
    public String editExamAnnouncement(@PathVariable Long id, @ModelAttribute Announcement announcement, RedirectAttributes redirectAttributes) {
        announcement.setId(id);
        announcementService.save(announcement);
        redirectAttributes.addFlashAttribute("success", "公告更新成功");
        return "redirect:/admin/exam-announcements";
    }


    @GetMapping("/admin/delete-exam-announcement/{id}")
    public String deleteExamAnnouncement(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        announcementService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "公告删除成功");
        return "redirect:/admin/exam-announcements";
    }

    @GetMapping("/admin/exam-status")
    public String examStatus(Model model) {
        Map<ExamRoom, List<Exam>> examsByRoom = examRoomService.getExamsByRoom();
        model.addAttribute("examsByRoom", examsByRoom);
        return "admin-exam-status";
    }

    @GetMapping("/admin/exam-notifications")
    public String showExamNotifications(Model model) {
        List<Exam> examsWithNonEmptyReportedStudentUsername = examService.findExamsWithNonEmptyReportedStudentUsername();
        model.addAttribute("exams", examsWithNonEmptyReportedStudentUsername);
        return "admin-exam-notifications";
    }
}
