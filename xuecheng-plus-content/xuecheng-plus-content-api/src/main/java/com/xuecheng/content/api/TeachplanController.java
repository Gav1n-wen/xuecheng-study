package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(value = "课程计划编辑接口",tags = "课程计划编辑接口")
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;
    @ApiOperation("查询课程计划树形结构")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable long courseId) {
        return teachplanService.findTeachplayTree(courseId);
    }

    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan( @RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
    }

    @ApiOperation("删除课程计划信息")
    @DeleteMapping("/teachplan/{id}")
    public void deletTeachplan(@PathVariable long id) {
        teachplanService.deleteTeachplan(id);
    }

    @ApiOperation("课程排序下移")
    @PostMapping("/teachplan/movedown/{id}")
    public void downTeachplan(@PathVariable long id) {
        teachplanService.downTeachplan(id);
    }

    @ApiOperation("课程排序上移")
    @PostMapping("/teachplan/moveup/{id}")
    public void upTeachplan(@PathVariable long id) {
        teachplanService.upTeachplan(id);
    }
}
