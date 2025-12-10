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
    public List<TeacherDto> getAll() {

        if (!teachers.isEmpty()) {
            return teachers.stream().map(teacher -> teacher.convert()).toList();

        }
        return new ArrayList<>();
    }

    @PostMapping("/add")
    public String add(@RequestBody TeacherDto teacherDto) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (teacherDto.getFirstName().isEmpty() || teacherDto.getFirstName().length() < 2 ||
                teacherDto.getFirstName().length() > 50) {
            return  "First name must be 2-50 characters and contain";
        }
        if (teacherDto.getLastName().isEmpty() || teacherDto.getLastName().length() < 2 ||
                teacherDto.getLastName().length() > 50) {
            return "Last name must be 2-50 characters and contain only letters";
        }
        if (teacherDto.getSubject().isEmpty()) {
            return "Subject is required";
        }
        if (teacherDto.getExperience() < 0 || teacherDto.getExperience() > 50) {
            return "Experience must be between 0 and 50 years";
        }
        if (teacherDto.getSalary() < 0 || teacherDto.getSalary() > 100000) {
            return "Salary must be between 0 and 100000";
        }
        if (teacherDto.getEmail() == null && teacherDto.getEmail().isEmpty() && teacherDto.getEmail().matches(emailRegex)) {
        } else if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName())) &&
                teachers.stream().anyMatch(teacher -> teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()))) {
            teachers.add(teacherDto.convert());
            return "success";
        }
        return "fail";
    }


}

