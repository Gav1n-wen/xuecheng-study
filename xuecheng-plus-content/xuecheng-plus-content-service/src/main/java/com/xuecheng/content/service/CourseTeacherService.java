package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    List<AddCourseTeacherDto> getCourseTeacher(long id);
    CourseTeacher saveCourseTeacher(AddCourseTeacherDto courseTeacherDto);
}
