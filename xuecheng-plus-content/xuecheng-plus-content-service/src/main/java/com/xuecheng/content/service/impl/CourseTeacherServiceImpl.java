package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.AddCourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Override
    public List<AddCourseTeacherDto> getCourseTeacher(long id) {
        List<AddCourseTeacherDto> courseTeacherDtoList = new ArrayList<>();
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, id);
        List<CourseTeacher> courseTeacherList = courseTeacherMapper.selectList(queryWrapper);

        for (CourseTeacher courseTeacher : courseTeacherList) {
            AddCourseTeacherDto courseTeacherDto = new AddCourseTeacherDto();
            BeanUtils.copyProperties(courseTeacher, courseTeacherDto);
            courseTeacherDtoList.add(courseTeacherDto);
        }

        return courseTeacherDtoList;
    }

    @Override
    @Transactional
    public CourseTeacher saveCourseTeacher(AddCourseTeacherDto courseTeacherDto) {
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
        int insert = courseTeacherMapper.insert(courseTeacher);
        if (insert <= 0) {
            XueChengPlusException.cast("添加老师失败");
        }
        return courseTeacher;
    }
}
