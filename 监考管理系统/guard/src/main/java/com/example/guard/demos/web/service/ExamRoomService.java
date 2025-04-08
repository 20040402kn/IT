package com.example.guard.demos.web.service;

import com.example.guard.demos.web.entity.Exam;
import com.example.guard.demos.web.entity.ExamRoom;
import com.example.guard.demos.web.repository.ExamRepository;
import com.example.guard.demos.web.repository.ExamRoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExamRoomService {
    private static final Logger logger = LoggerFactory.getLogger(ExamRoomService.class);

    @Autowired
    private ExamRoomRepository examRoomRepository;

    @Autowired
    private ExamRepository examRepository;

    @PostConstruct
    public void init() {
        migrateExamLocationsToExamRooms();
    }

    @Transactional
    public void migrateExamLocationsToExamRooms() {
        List<Exam> exams = examRepository.findAll();
        for (Exam exam : exams) {
            String location = exam.getLocation();
            // 假设 location 格式为 "楼号-教室号"
            String[] parts = location.split("-");
            if (parts.length == 2) {
                String building = parts[0];
                String roomNumber = parts[1];

                // 检查是否已经存在相同的 ExamRoom
                ExamRoom existingExamRoom = examRoomRepository.findByBuildingAndRoomNumber(building, roomNumber);
                if (existingExamRoom == null) {
                    ExamRoom examRoom = new ExamRoom();
                    examRoom.setBuilding(building);
                    examRoom.setRoom_number(roomNumber);
                    // 假设每个教室的容量为 50，你可以根据实际情况调整
                    examRoom.setCapacity(50);

                    ExamRoom savedExamRoom = examRoomRepository.save(examRoom);
                    exam.setRoom(savedExamRoom);
                    examRepository.save(exam);
                    logger.info("成功迁移考试地点: {}", location);
                } else {
                    // 如果已经存在，直接关联
                    exam.setRoom(existingExamRoom);
                    examRepository.save(exam);
                    logger.info("考试地点已存在，关联现有记录: {}", location);
                }
            } else {
                // 添加日志记录解析失败的情况
                logger.warn("无法解析 location: {}", location);
            }
        }
    }

    public List<ExamRoom> getAllExamRooms() {
        return examRoomRepository.findAll();
    }

    public Map<ExamRoom, List<Exam>> getExamsByRoom() {
        List<ExamRoom> examRooms = getAllExamRooms();
        Map<ExamRoom, List<Exam>> examsByRoom = new HashMap<>();
        for (ExamRoom room : examRooms) {
            List<Exam> exams = examRepository.findByRoom(room);
            examsByRoom.put(room, exams);
        }
        return examsByRoom;
    }
}
