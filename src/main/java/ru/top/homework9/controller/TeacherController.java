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
//      teachers.add(new Teacher("Дмитрий","Сорокин","математика",5,200000.0,"sorokindmitrijj2@rambler.ru",true));
//      teachers.add(new Teacher("Иван","Иванов","биология",4,150000.0,"triobjtrjbs2@rambler.ru", true));
    }

    @GetMapping("/all")
    public List<TeacherDto>getAll(){

        if(!teachers.isEmpty()){
        return teachers.stream().map(teacher -> teacher.convert()).toList();

        }
       return new ArrayList<>();
    }
@PostMapping("/add")
    public String add(@RequestBody TeacherDto teacherDto) {
    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    if (!teacherDto.getFirstName().isEmpty() && teacherDto.getFirstName().length() >= 2 &&
            teacherDto.getFirstName().length() <= 50) {

//        teachers.add(teacherDto.convert());
//        return "success";
    } else if (!teacherDto.getLastName().isEmpty() && teacherDto.getLastName().length() >= 2 &&
        teacherDto.getLastName().length()>=50){
    } else if (!teacherDto.getSubject().isEmpty()) {
        
    } else if (teacherDto.getExperience()>=0 && teacherDto.getExperience()<=50) {
    } else if (teacherDto.getSalary()>=0 && teacherDto.getSalary()<=100000) {
    } else if(teacherDto.getEmail()!=null&&!teacherDto.getEmail().isEmpty()&&!teacherDto.getEmail().matches(emailRegex)){
    } else if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName()))) {
        
    }


    return "fail";
    }


}

