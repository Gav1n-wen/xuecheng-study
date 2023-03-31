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
    public List<CourseTeacher> getCourseTeacher(long id) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, id);
        List<CourseTeacher> courseTeacherList = courseTeacherMapper.selectList(queryWrapper);

        return courseTeacherList;
    }

    @Override
    @Transactional
    public CourseTeacher saveCourseTeacher(CourseTeacher courseTeacher) {
        int insert = courseTeacherMapper.insert(courseTeacher);
        if (insert <= 0) {
            XueChengPlusException.cast("添加老师失败");
        }
        long id = courseTeacher.getId();
        return courseTeacherMapper.selectById(id);
    }

    @Override
    @Transactional
    public CourseTeacher updateCourseTeacher(Long companyID, CourseTeacher courseTeacher) {
        long id = courseTeacher.getId();
        if (courseTeacherMapper.selectById(id) == null) {
            XueChengPlusException.cast("该老师不存在，更新失败");
        }
        if (!companyID.equals(courseTeacherMapper.getCompanyId(id))) {
            XueChengPlusException.cast("只允许修改本机构老师信息");
        }
        int update = courseTeacherMapper.updateById(courseTeacher);
        if (update <= 0) {
            XueChengPlusException.cast("老师信息更新失败");
        }
        return courseTeacher;
    }

    @Override
    @Transactional
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId, teacherId)
                    .eq(CourseTeacher::getCourseId, courseId);
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete <= 0) {
            XueChengPlusException.cast("抱歉，删除失败");
        }
    }
}
