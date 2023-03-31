package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "课程老师编辑接口",tags = "课程老师编辑接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;
    @GetMapping("/courseTeacher/list/{id}")
    @ApiOperation("查询所有老师")
    public List<CourseTeacher> getCourseTeacher(@PathVariable long id) {
        return courseTeacherService.getCourseTeacher(id);
    }

    @PostMapping ("/courseTeacher")
    @ApiOperation("添加或修改老师")
    public CourseTeacher saveOrUpdateCourseTeacher(@Validated @RequestBody CourseTeacher courseTeacher) {
        Long id = courseTeacher.getId();
        if (id == null) {
            return courseTeacherService.saveCourseTeacher(courseTeacher);
        }
        Long companyId = 1232141425L;
        return courseTeacherService.updateCourseTeacher(companyId, courseTeacher);
    }

    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    @ApiOperation("删除老师")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long teacherId) {
        System.out.println("here");
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}
