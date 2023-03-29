package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class TeachplanServiceImpl implements TeachplanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto> findTeachplayTree(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        Long id = teachplanDto.getId();
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            int update = teachplanMapper.updateById(teachplan);
            if (update <= 0) {
                XueChengPlusException.cast("课程更新失败");
            }
        } else {
            int courseCount = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplan.setOrderby(courseCount + 1);
            teachplanMapper.insert(teachplan);
        }
    }

    @Override
    @Transactional
    public void deleteTeachplan(Long id) {
        if (getSubTeachplan(id) > 0) {
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }
        int delete = teachplanMapper.deleteById(id);
        if (delete <= 0) {
            XueChengPlusException.cast("课程删除失败");
        }
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, id);
        int count = teachplanMediaMapper.selectCount(queryWrapper);
        if (count > 0) {
            int insert = teachplanMediaMapper.delete(queryWrapper);
            if (insert != count) {
                XueChengPlusException.cast("课程删除时未能成功删除视频");
            }
        }
    }

    @Override
    @Transactional
    public void downTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        int orderBy = teachplan.getOrderby();

        int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
        if (count == teachplan.getOrderby()) {
            XueChengPlusException.cast("该课程是本章最后一节，无法再下移");
        }

        Teachplan nextTeachplan = getSameChapterTeachplan(teachplan.getCourseId(), teachplan.getParentid(), orderBy + 1);

        teachplan.setOrderby(orderBy + 1);
        int update = teachplanMapper.updateById(teachplan);
        if (update <= 0) {
            XueChengPlusException.cast("课程下移失败");
        }

        nextTeachplan.setOrderby(orderBy);
        int updateNext = teachplanMapper.updateById(nextTeachplan);
        if (updateNext <= 0) {
            XueChengPlusException.cast("课程下移失败");
        }
    }

    @Override
    public void upTeachplan(Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        int orderBy = teachplan.getOrderby();
        if (orderBy == 1) {
            XueChengPlusException.cast("该课程是本章第一节，无法上移");
        }
        Teachplan preTeachplan = getSameChapterTeachplan(teachplan.getCourseId(), teachplan.getParentid(), orderBy - 1);

        teachplan.setOrderby(orderBy - 1);
        int update = teachplanMapper.updateById(teachplan);
        if (update <= 0) {
            XueChengPlusException.cast("课程上移失败");
        }

        preTeachplan.setOrderby(orderBy);
        int updateNext = teachplanMapper.updateById(preTeachplan);
        if (updateNext <= 0) {
            XueChengPlusException.cast("课程上移失败");
        }
    }

    private int getSubTeachplan(long id) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid, id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    private int getTeachplanCount(long courseId,long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    private Teachplan getSameChapterTeachplan(long courseId, long parentId, long orderBy) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        queryWrapper.eq(Teachplan::getOrderby, orderBy);
        return teachplanMapper.selectOne(queryWrapper);
    }

}
