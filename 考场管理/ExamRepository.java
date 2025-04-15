package com.example.guard.demos.web.repository;

import com.example.guard.demos.web.entity.Exam;
import com.example.guard.demos.web.entity.ExamRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByTeacherId(Long teacherId);
    @Query("SELECT e FROM Exam e WHERE e.subject LIKE %:searchTerm% OR e.teacherId = :searchTerm")
    List<Exam> searchExams(@Param("searchTerm") String searchTerm);

    @Query("SELECT e FROM Exam e ORDER BY e.time ASC")
    List<Exam> findTop8ByTimeAsc();

    @Query("SELECT e FROM Exam e WHERE e.subject LIKE %:subject% OR e.id = :id")
    List<Exam> findBySubjectOrId(@Param("subject") String subject, @Param("id") Long id);

    @Query("SELECT e FROM Exam e JOIN e.teacherIds t WHERE t IN :teacherIds")
    List<Exam> findByTeacherIdsIn(@Param("teacherIds") List<Long> teacherIds);

    @Query("SELECT e FROM Exam e JOIN e.teacherIds t WHERE t = :teacherId")
    List<Exam> findByTeacherIdInExamTeacherIds(@Param("teacherId") Long teacherId);

    @Query("SELECT e FROM Exam e JOIN e.room r WHERE r = :room")
    List<Exam> findByRoom(@Param("room") ExamRoom room);

    @Query("SELECT e FROM Exam e JOIN e.reports r WHERE r.student.username = :username")
    List<Exam> findExamsWithReportsByStudentUsername(@Param("username") String username);

    @Query("SELECT e FROM Exam e WHERE e.reports IS NOT EMPTY")
    List<Exam> findExamsWithReports();

    @Query("SELECT e FROM Exam e WHERE e.reportedStudentUsername IS NOT NULL")
    List<Exam> findExamsWithNonEmptyReportedStudentUsername();

    @Query("SELECT e FROM Exam e WHERE e.reportedStudentUsername = :reportedStudentUsername")
    List<Exam> findExamsWithReportsByReportedStudentUsername(@Param("reportedStudentUsername") String reportedStudentUsername);

    @Query("SELECT e FROM Exam e WHERE e.id IN :examIds AND e.reportedStudentUsername = :reportedStudentUsername")
    List<Exam> findByIdInAndReportedStudentUsername(@Param("examIds") List<Long> examIds, @Param("reportedStudentUsername") String reportedStudentUsername);


}
