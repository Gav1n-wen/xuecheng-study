package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yyw
 * @version 1.0
 * @description TODO
 * @date 2023-03-24 17:41
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNode(id);
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();

        Map<String, CourseCategoryTreeDto> indexMap = new HashMap<>();
        for (CourseCategoryTreeDto courseItem: courseCategoryTreeDtos) {
            if (id.equals(courseItem.getId())) continue;
            String courseItemId = courseItem.getId();
            if (id.equals(courseItem.getParentid())) {
                if (!indexMap.containsKey(courseItemId)) {
                    indexMap.put(courseItemId, courseItem);
                    courseCategoryTreeDtoList.add(courseItem);
                }
            } else {
                CourseCategoryTreeDto currentCourseCategory = indexMap.get(courseItem.getParentid());
                if (currentCourseCategory.getChildrenTreeNodes() == null) {
                    currentCourseCategory.setChildrenTreeNodes(new ArrayList<>());
                }
                currentCourseCategory.getChildrenTreeNodes().add(courseItem);
            }
        }

        return courseCategoryTreeDtoList;
    }

}
