package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程老师编辑接口",tags = "课程老师编辑接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;
    @GetMapping("/courseTeacher/list/{id}")
    @ApiOperation("查询所有老师")
    public List<AddCourseTeacherDto> getCourseTeacher(@PathVariable long id) {
        return courseTeacherService.getCourseTeacher(id);
    }

    @PostMapping ("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody AddCourseTeacherDto courseTeacherDto) {
        return courseTeacherService.saveCourseTeacher(courseTeacherDto);
    }
}
