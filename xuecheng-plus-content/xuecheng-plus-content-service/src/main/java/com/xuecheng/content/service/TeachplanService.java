package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TeachplanService {
    public List<TeachplanDto> findTeachplayTree(Long courseId);
    public void saveTeachplan(SaveTeachplanDto teachplanDto);
    public void deleteTeachplan(Long id);
    public void downTeachplan(Long id);
    public void upTeachplan(Long id);
}
