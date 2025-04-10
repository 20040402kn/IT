package com.example.guard.demos.web.repository;

import com.example.guard.demos.web.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRoomRepository extends JpaRepository<ExamRoom, Long> {
    @Query("SELECT er FROM ExamRoom er WHERE er.building = :building AND er.room_number = :roomNumber")
    ExamRoom findByBuildingAndRoomNumber(@Param("building") String building, @Param("roomNumber") String roomNumber);

}
