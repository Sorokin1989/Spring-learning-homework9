package ru.top.homework9.models;

import ru.top.homework9.dto.TeacherDto;

public class Teacher {
    //id: Integer
    //firstName: String (Имя)
    //lastName: String (Фамилия)
    //subject: String (Предмет: Math, Physics, English, etc.)
    //experience: Integer (Стаж работы в годах)
    //salary: Double (Зарплата)
    //email: String (Email)
    //isActive: Boolean (Активен/уволен)
    private Integer id;
    private String firstName;
    private String lastName;
    private String subject;
    private Integer experience;
    private Double salary;
    private String email;
    private Boolean isActive;
    private static Integer count=1;

    public Teacher(String firstName, String lastName, String subject, Integer experience, Double salary, String email, Boolean isActive) {
        this.id = count++;
        this.firstName = firstName;
        this.lastName = lastName;
        this.subject = subject;
        this.experience = experience;
        this.salary = salary;
        this.email = email;
        this.isActive = isActive;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
    public TeacherDto convert(){
        return new TeacherDto(this.firstName, this.lastName, this.subject, this.experience, this.salary, this.email, this.isActive);
    }
}
