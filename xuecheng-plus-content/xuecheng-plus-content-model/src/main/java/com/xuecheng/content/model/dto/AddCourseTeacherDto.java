package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "CourseTeacherDto", description = "添加教师基本信息")
public class AddCourseTeacherDto {
    /**
     * 课程号
     */
    Long courseId;
    /**
     * 老师姓名
     */
    String teacherName;
    /**
     *  教师职位
     */
    String position;
    /**
     * 教师简介
     */
    String introduction;
    /**
     * 教师照片
     */
    String photograph;
}
