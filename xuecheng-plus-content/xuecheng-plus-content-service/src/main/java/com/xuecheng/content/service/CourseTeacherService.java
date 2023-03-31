package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    List<CourseTeacher> getCourseTeacher(long id);
    CourseTeacher saveCourseTeacher(CourseTeacher courseTeacherDto);
    CourseTeacher updateCourseTeacher(Long companyId, CourseTeacher courseTeacher);
    void deleteCourseTeacher(Long courseId, Long teacherId);
}
