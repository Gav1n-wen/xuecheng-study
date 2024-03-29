package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yyw
 * @version 1.0
 * @description TODO
 * @date 2023-03-21 11:24
 */
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    CourseMarketServiceImpl courseMarketService;


    @Transactional
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据课程名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        // 根据课程状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        // 根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(pageResult.getRecords(),
                pageResult.getTotal(),
                pageParams.getPageNo(),
                pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        CourseBase courseBaseNew = new CourseBase();
        // 将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        int courseBaseInsert = courseBaseMapper.insert(courseBaseNew);
        if (courseBaseInsert <= 0) {
            throw new RuntimeException("添加课程基本信息失败");
        }

        // 数据库添加CourseMarket表
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);

        String chargeRule = addCourseDto.getCharge();
        if ("201001".equals(chargeRule)) {
            Float price = addCourseDto.getPrice();
            if (price == null || price <= 0) {
                throw new XueChengPlusException("收费价格必须大于0");
            }
        }
        int courMarketInsert = courseMarketMapper.insert(courseMarketNew);
        if (courMarketInsert <= 0) {
            throw new RuntimeException("新增课程营销信息失败");
        }

        return getCourseBaseInfo(courseId);
    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseBase == null) return null;
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        Long courseId = dto.getId();
        CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
        if (!companyId.equals(courseBaseUpdate.getCompanyId())) {
            XueChengPlusException.cast("只允许修改本机构的课程");
        }
        BeanUtils.copyProperties(dto, courseBaseUpdate);

        courseBaseUpdate.setChangeDate(LocalDateTime.now());
        int update = courseBaseMapper.updateById(courseBaseUpdate);
        if (update <= 0) {
            XueChengPlusException.cast("课程信息修改失败");
        }

        CourseMarket courseMarketUpdate = courseMarketMapper.selectById(courseId);
        if (courseMarketUpdate == null) {
            courseMarketUpdate = new CourseMarket();
        }

        courseMarketUpdate.setId(courseId);
        BeanUtils.copyProperties(dto, courseMarketUpdate);
        int updateMarket = saveCourseMarket(courseMarketUpdate);

        if (updateMarket <= 0) {
            XueChengPlusException.cast("课程信息修改失败");
        }
        return getCourseBaseInfo(courseId);
    }

    @Override
    @Transactional
    public void deleteCourseInfo(Long id) {
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (!"202002".equals(courseBase.getAuditStatus())) {
            XueChengPlusException.cast("只能删除未提交的课程");
        }
        deleteTeacherList(id);
        deleteTeachplanList(id);
        deleteCourseMarketList(id);
        int delete = courseBaseMapper.deleteById(id);
        if (delete <= 0) {
            XueChengPlusException.cast("课程删除失败，请重试");
        }
    }
    private int deleteTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> teacherQueryWrapper = new LambdaQueryWrapper<>();
        teacherQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.delete(teacherQueryWrapper);
    }
    private int deleteTeachplanList(Long courseId) {
        LambdaQueryWrapper<Teachplan> teachplanQueryWrapper = new LambdaQueryWrapper<>();
        teachplanQueryWrapper.eq(Teachplan::getCourseId, courseId);
        return teachplanMapper.delete(teachplanQueryWrapper);
    }
    private int deleteCourseMarketList(Long courseId) {
        return courseMarketMapper.deleteById(courseId);
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            XueChengPlusException.cast("请设置收费规则");
        }

        if (charge.equals("201001")) {
            Float price = courseMarket.getPrice();
            if (price == null || price <= 0) {
                XueChengPlusException.cast("收费价格必须大于0");
            }
        }
        boolean save = courseMarketService.saveOrUpdate(courseMarket);
        return save ? 1 : -1;
    }

}
