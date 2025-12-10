package ru.top.homework9.controller;

import org.springframework.web.bind.annotation.*;
import ru.top.homework9.dto.TeacherDto;
import ru.top.homework9.models.Teacher;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    List<Teacher> teachers;

    public TeacherController() {
      teachers = new ArrayList<>();
      teachers.add(new Teacher("Дмитрий","Сорокин","математика",5,200000.0,"sorokindmitrijj2@rambler.ru",true));
      teachers.add(new Teacher("Иван","Иванов","биология",4,150000.0,"triobjtrjbs2@rambler.ru", true));
    }

    @GetMapping("/all")
    public List<TeacherDto>getAll(){

        if(!teachers.isEmpty()){
        return teachers.stream().map(teacher -> teacher.convert()).toList();

        }
       return new ArrayList<>();
    }
@PostMapping("/add")
    public String add(@RequestBody TeacherDto teacherDto){
if(!teacherDto.getFirstName().isEmpty() && !teacherDto.getLastName().isEmpty()){

    teachers.add(teacherDto.convert());
    return "seccess";
}


    return "fail";
    }


}

