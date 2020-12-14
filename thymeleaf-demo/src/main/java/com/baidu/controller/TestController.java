package com.baidu.controller;

import com.baidu.entity.Student;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Controller
public class TestController {


    @GetMapping(value = "test")
    public String test(ModelMap map){
        map.put("name","tomcat");
        return "index";
    }

    @GetMapping(value = "stu")
    public String student(ModelMap map){
        Student student = new Student();
        student.setCode("008");
        student.setPass("1234");
        student.setAge(18);
        student.setLikeColor("<font color='red'>红色</font>");

        map.put("stu",student);
        return "index";
    }

    @GetMapping(value = "list")
    public String list(ModelMap map){

        Student student = new Student();
        student.setCode("009");
        student.setPass("123");
        student.setAge(19);
        student.setLikeColor("<font style='color:yellow'>yellow</font>");
        Student student2 = new Student();
        student2.setCode("010");
        student2.setPass("124");
        student2.setAge(21);
        student2.setLikeColor("<font style='color:brown'>brown</font>");

        map.put("list", Arrays.asList(student,student2));
       return "index";
    }


}
