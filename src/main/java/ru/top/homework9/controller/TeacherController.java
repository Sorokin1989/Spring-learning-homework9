package ru.top.homework9.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.top.homework9.dto.TeacherDto;
import ru.top.homework9.models.Teacher;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private List<Teacher> teachers;
    private static String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
//    List<TeacherDto> teacherDtos;

    public TeacherController() {
        teachers = new ArrayList<>();
        teachers.add(new Teacher("Дмитрий", "Сорокин", "математика", 5, 90000.0, "sorokindmitrijj2@rambler.ru", true));
        teachers.add(new Teacher("Иван", "Иванов", "биология", 4, 15000.0, "triobjtrjbs2@rambler.ru", false));
    }

    @GetMapping("/all")
    public List<TeacherDto> getAll() {

        if (!teachers.isEmpty()) {
            return teachers.stream().map(teacher -> teacher.convert()).toList();

        }
        return new ArrayList<>();
    }

    @GetMapping("/{id}")
    public TeacherDto getById(@PathVariable(name = "id") Integer id) {
        for (Teacher teacher : teachers) {
            if (id != null && id > 0 && id.equals(teacher.getId())) {
                return teacher.convert();
            }

        }
//            return new TeacherDto();
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Teacher with id " + id + " not found");
    }

    @GetMapping("/search")
    public List<TeacherDto> searchTeacher(@RequestParam(required = false, name = "name") String firstName,
                                          @RequestParam(required = false, name = "surname") String lastName) {

        return teachers.stream().filter(teacher -> {
            boolean nameMatches = firstName == null || firstName.isEmpty() || (teacher.getFirstName() != null
                    && teacher.getFirstName().equalsIgnoreCase(firstName));

            boolean surnameMatches = lastName == null || lastName.isEmpty() || (teacher.getLastName() != null
                    && teacher.getLastName().equalsIgnoreCase(lastName));

            return nameMatches && surnameMatches;
        }).map(teacher -> teacher.convert()).toList();


    }

    @GetMapping("/subject/{subject}")
    public List<TeacherDto> getTeacherSubject(@PathVariable String subject) {
        if (teachers.isEmpty()) return new ArrayList<>();

        return teachers.stream().filter(teacher ->
                teacher.getSubject() != null && teacher.getSubject().equalsIgnoreCase(subject)).map(teacher -> teacher.convert()).toList();

    }

    @GetMapping("/filter")
    public List<TeacherDto> getTeachersFiltered(
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false) Integer maxExp,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary
    ) {
        if (minExp == null && maxExp == null && minSalary == null && maxSalary == null) {
            return new ArrayList<>();
        }
        if ((minExp != null && maxExp != null && minExp > maxExp) || (minSalary != null && maxSalary != null && minSalary > maxSalary)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error! Минимальное значение не может быть больше максимального.");
        }

        return teachers.stream().filter(teacher -> {
            if (minExp != null || maxExp != null) {
                if (teacher.getExperience() == null) {
                    return false;
                }
                if (minExp != null && teacher.getExperience() < minExp) {
                    return false;
                }
                if (maxExp != null && teacher.getExperience() > maxExp) {
                    return false;
                }
            }

            if (minSalary != null || maxSalary != null) {
                if (teacher.getSalary() == null) {
                    return false;
                }
                if (minSalary != null && teacher.getSalary() < minSalary) {
                    return false;
                }
                if (maxSalary != null && teacher.getSalary() > maxSalary) {
                    return false;
                }
            }
            return true;
        }).map(teacher -> teacher.convert()).toList();

    }

    @GetMapping("/active")
    public List<TeacherDto> getActiveTeachers() {
        return teachers.stream().filter(teacher -> teacher.getActive() == true).map(teacher -> teacher.convert()).toList();

    }

    @GetMapping("/count")
    public Integer getCount() {
        if (teachers != null && !teachers.isEmpty()) {
            return teachers.size();
        } else
            return 0;
    }

    @GetMapping("/count-by-subject")
    public Map<String, Integer> countBySubject() {
        if (teachers == null) {
            return new HashMap<>();
        }

        return teachers.stream().filter(teacher -> teacher != null && teacher.getSubject() != null).collect(Collectors.toMap(teacher -> teacher.getSubject(),
                teacher -> 1, (a, b) -> a + b));
    }


    @PostMapping("/add")
    public String add(@RequestBody TeacherDto teacherDto) {
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (teacherDto.getFirstName() == null || teacherDto.getFirstName().isEmpty() || teacherDto.getFirstName().length() < 2 ||
                teacherDto.getFirstName().length() > 50) {
            return "Fail! First name must be 2-50 characters and contain";
        }
        if (teacherDto.getLastName() == null || teacherDto.getLastName().isEmpty() || teacherDto.getLastName().length() < 2 ||
                teacherDto.getLastName().length() > 50) {
            return "Fail! Last name must be 2-50 characters and contain only letters";
        }
        if (teacherDto.getSubject() == null || teacherDto.getSubject().isEmpty()) {
            return "Fail! Subject is required";
        }
        if (teacherDto.getExperience() < 0 || teacherDto.getExperience() > 50) {
            return "Fail! Experience must be between 0 and 50 years";
        }
        if (teacherDto.getSalary() < 0 || teacherDto.getSalary() > 100000) {
            return "Fail! Salary must be between 0 and 100000";
        }
        if (teacherDto.getEmail() == null || teacherDto.getEmail().isEmpty() || !teacherDto.getEmail().matches(emailRegex)) {
            return "Fail! Invalid email format";
        }
        if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName()) &&
                teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()))) {
            return "Fail! Teacher with this name already exists";
        }
        teachers.add(teacherDto.convert());
        return "success";
    }

    @PostMapping("/add-bulk")
    public String addAll(@RequestBody List<TeacherDto> newTeacherDto) {
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (teachers == null) {
            teachers = new ArrayList<>();
        }
        if (newTeacherDto == null) {
            return "Error! Request body is null";
        }
        if (newTeacherDto.isEmpty()) {
            return "Error! Empty list";
        }
        Set<String> names = teachers.stream().map(teacher -> teacher.getFirstName().toLowerCase() + " " + teacher.getLastName().toLowerCase()).collect(Collectors.toSet());
        Set<String> batchNames = new HashSet<>();
        List<Teacher> filterNewTeacherDto = newTeacherDto.stream().filter(teacherDto -> teacherDto != null).
                filter(teacherDto -> {

                    if (teacherDto.getFirstName() == null || teacherDto.getFirstName().isEmpty() || teacherDto.getFirstName().length() < 2 ||
                            teacherDto.getFirstName().length() > 50) {
                        return false;
                    }
                    if (teacherDto.getLastName() == null || teacherDto.getLastName().isEmpty() || teacherDto.getLastName().length() < 2 ||
                            teacherDto.getLastName().length() > 50) {
                        return false;
                    }

//                    if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName()) &&
//                            teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()))) {
//                        return false;
//                    }

                    if (teacherDto.getSubject() == null || teacherDto.getSubject().isEmpty()) {
                        return false;
                    }
                    if (teacherDto.getExperience() < 0 || teacherDto.getExperience() > 50) {
                        return false;
                    }
                    if (teacherDto.getSalary() < 0 || teacherDto.getSalary() > 100000) {
                        return false;
                    }
                    if (teacherDto.getEmail() == null || teacherDto.getEmail().isEmpty() || !teacherDto.getEmail().matches(emailRegex)) {
                        return false;
                    }

                    String nameKey = teacherDto.getFirstName().toLowerCase() + " " + teacherDto.getLastName().toLowerCase();

                    if (names.contains(nameKey)) {
                        return false;
                    }

                    batchNames.add(nameKey);
                    names.add(nameKey);
                    return true;
                }).map(teacherDto -> teacherDto.convert()).toList();


        if (filterNewTeacherDto.isEmpty()) {
            return "Error! No valid teachers found after validation";
        }

        int count = filterNewTeacherDto.size();

        teachers.addAll(filterNewTeacherDto);
        return "Added " + count + " teachers";


    }

    @PutMapping("/update/{id}")
    public String updateAll(@RequestBody TeacherDto teacherDto, @PathVariable Integer id) {
//        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


        if (teachers == null) {
            return "Error! Teachers list is not initialized";
        }

        if (teacherDto == null) {
            return "Error! Request body is null";
        }
        if (id == null || id < 0) {
            return "Error! Invalid id";
        }
        Teacher updateTeacher = teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);

        if (updateTeacher == null) {
            return "Teacher with id " + id + " not found";
        }


        if (teacherDto.getFirstName() == null || teacherDto.getFirstName().isEmpty() || teacherDto.getFirstName().length() < 2 ||
                teacherDto.getFirstName().length() > 50) {
            return "Fail! First name must be 2-50 characters";
        }
//            if (updateTeacher.getFirstName()==null||!updateTeacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName())) {
//
//            wasUpdated = true;
//            }
//        }
        if (teacherDto.getLastName() == null || teacherDto.getLastName().isEmpty() || teacherDto.getLastName().length() < 2 ||
                teacherDto.getLastName().length() > 50) {
            return "Fail! Last name must be 2-50 characters";
//            if(updateTeacher.getLastName()==null||!updateTeacher.getLastName().equalsIgnoreCase(teacherDto.getLastName())) {
//
//            wasUpdated = true;
//            }
        }

//                    if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName()) &&
//                            teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()))) {
//                        return false;
//                    }

        if (teacherDto.getSubject() == null || teacherDto.getSubject().isEmpty()) {
            return "Fail! Subject is required";

//            if(updateTeacher.getSubject()==null||!updateTeacher.getSubject().equalsIgnoreCase(teacherDto.getSubject())) {
//
//            wasUpdated = true;
//            }
        }
        if (teacherDto.getExperience() == null || teacherDto.getExperience() < 0 || teacherDto.getExperience() > 50) {
            return "Fail! Experience must be between 0 and 50 years";
//            if(updateTeacher.getExperience()==null||!updateTeacher.getExperience().equals(teacherDto.getExperience())) {
//
//            wasUpdated = true;
//            }
        }
        if (teacherDto.getSalary() == null || teacherDto.getSalary() < 0 || teacherDto.getSalary() > 100000) {
            return "Fail! Salary must be between 0 and 100000";
//            if(updateTeacher.getSalary()==null||!updateTeacher.getSalary().equals(teacherDto.getSalary())) {
//
//            wasUpdated = true;
//            }
        }
        if (teacherDto.getEmail() == null || teacherDto.getEmail().isEmpty() || !teacherDto.getEmail().matches(emailRegex)) {
            return "Fail! Invalid email format";
//            if(updateTeacher.getEmail()==null||!updateTeacher.getEmail().equals(teacherDto.getEmail())) {
//
//            wasUpdated = true;
//            }
        }

        if (teacherDto.getActive() == null) {
            return "Fail! Active is null";
        }


        boolean duplicateName = teachers.stream().filter(teacher -> !teacher.getId().equals(id)).anyMatch(teacher -> teacher.getFirstName() != null && teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName())
                && teacher.getLastName() != null && teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()));

        if (duplicateName) {
            return "Error! Teacher with name " + teacherDto.getFirstName() + " " +
                    teacherDto.getLastName() + " already exists";
        }


        boolean duplicateEmail = teachers.stream().filter(teacher -> !teacher.getId().equals(id)).anyMatch(teacher -> teacher.getEmail() != null && teacher.getEmail().equalsIgnoreCase(teacherDto.getEmail()));
        if (duplicateEmail) {
            return "Error! Teacher with email " + teacherDto.getEmail() + " already exists";
        }

        updateTeacher.setFirstName(teacherDto.getFirstName());
        updateTeacher.setLastName(teacherDto.getLastName());
        updateTeacher.setSubject(teacherDto.getSubject());
        updateTeacher.setExperience(teacherDto.getExperience());
        updateTeacher.setSalary(teacherDto.getSalary());
        updateTeacher.setEmail(teacherDto.getEmail());
        updateTeacher.setActive(teacherDto.getActive());

        return "success";


    }

    @PatchMapping("/update-partial/{id}")
    public String updatePartial(@RequestBody TeacherDto teacherDto, @PathVariable Integer id) {
//    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (teachers == null) {
            return "Error! Teachers list is not initialized";
        }
        if (teacherDto == null) {
            return "Error! Request body is null";
        }
        if (id == null || id < 0) {
            return "Error! Invalid id";
        }

        Teacher updateTeacher = teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);
        if (updateTeacher == null) {
            return "Error! Teacher with id " + id + " not found";
        }


        boolean wasUpdated = false;


        if (teacherDto.getFirstName() != null && !teacherDto.getFirstName().isEmpty() && teacherDto.getFirstName().length() >= 2 &&
                teacherDto.getFirstName().length() <= 50) {
            if (updateTeacher.getFirstName() == null || !updateTeacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName())) {
                updateTeacher.setFirstName(teacherDto.getFirstName());
                wasUpdated = true;
            }

        }

        if (teacherDto.getLastName() != null && !teacherDto.getLastName().isEmpty() && teacherDto.getLastName().length() >= 2 &&
                teacherDto.getLastName().length() <= 50) {
            if (updateTeacher.getLastName() == null || !updateTeacher.getLastName().equalsIgnoreCase(teacherDto.getLastName())) {
                updateTeacher.setLastName(teacherDto.getLastName());
                wasUpdated = true;
            }

        }


        if (teacherDto.getFirstName() != null && teacherDto.getLastName() != null) {
            boolean duplicateName = teachers.stream().filter(teacher -> !teacher.getId().equals(id)).anyMatch(teacher -> teacher.getFirstName() != null && teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName())
                    && teacher.getLastName() != null && teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()));

            if (duplicateName) {
                return "Error! Teacher with name " + teacherDto.getFirstName() + " " +
                        teacherDto.getLastName() + " already exists";
            }
        }


//                    if (teachers.stream().anyMatch(teacher -> teacher.getFirstName().equalsIgnoreCase(teacherDto.getFirstName()) &&
//                            teacher.getLastName().equalsIgnoreCase(teacherDto.getLastName()))) {
//                        return false;
//                    }

        if (teacherDto.getSubject() != null && !teacherDto.getSubject().isEmpty()) {
            if (updateTeacher.getSubject() == null || !updateTeacher.getSubject().equalsIgnoreCase(teacherDto.getSubject())) {
                updateTeacher.setSubject(teacherDto.getSubject());
                wasUpdated = true;
            }


        }
        if (teacherDto.getExperience() != null && teacherDto.getExperience() >= 0 && teacherDto.getExperience() <= 50) {
            if (updateTeacher.getExperience() == null || !updateTeacher.getExperience().equals(teacherDto.getExperience())) {
                updateTeacher.setExperience(teacherDto.getExperience());
                wasUpdated = true;
            }
        }
        if (teacherDto.getSalary() != null && teacherDto.getSalary() >= 0 && teacherDto.getSalary() <= 100000) {
            if (updateTeacher.getSalary() == null || !updateTeacher.getSalary().equals(teacherDto.getSalary())) {
                updateTeacher.setSalary(teacherDto.getSalary());
                wasUpdated = true;
            }
        }
        if (teacherDto.getEmail() != null && !teacherDto.getEmail().isEmpty() && teacherDto.getEmail().matches(emailRegex)) {
            if (updateTeacher.getEmail() == null || !updateTeacher.getEmail().equals(teacherDto.getEmail())) {
                updateTeacher.setEmail(teacherDto.getEmail());
                wasUpdated = true;
            }
        }
        if (teacherDto.getActive() != null && !updateTeacher.getActive().equals(teacherDto.getActive())) {
            updateTeacher.setActive(teacherDto.getActive());
            wasUpdated = true;
        }

        if (wasUpdated) {
            return "success";
        }
        return "fail";


    }

    @PatchMapping("/deactivate/{id}")
    public String deactivateTeacher(@PathVariable Integer id) {
        if (teachers == null) {
            return "Error! Teachers list is not initialized";
        }
        if (id == null || id < 0) {
            return "Error! Invalid id";
        }

        Teacher findTeacher = teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);
        if (findTeacher == null) {
            return "Error! Teacher with id " + id + " not found";
        }
        if (findTeacher.getActive() != null && findTeacher.getActive().equals(true)) {
            findTeacher.setActive(false);
            return "success";
        }

        return "fail";
    }

    @PatchMapping("/activate/{id}")
    public String activateTeacher(@PathVariable Integer id) {
        if (teachers == null) {
            return "Error! Teachers list is not initialized";
        }
        if (id == null || id < 0) {
            return "Error! Invalid id";
        }
        Teacher findTeacher = teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);

        if (findTeacher == null) {
            return "Error! Teacher with id " + id + " not found";
        }
        if (findTeacher.getActive() != null && findTeacher.getActive().equals(false)) {
            findTeacher.setActive(true);
            return "success";
        }
        return "fail";
    }

    @PatchMapping("/increase-salary/{id}")
    public String increaseSalary(@PathVariable Integer id,
                                 @RequestParam Integer percent) {
        if (teachers == null) {
            return "Error! Teachers list is not initialized";
        }
        if (id == null || id < 0) {
            return "Error! Invalid id";
        }

        Teacher findTeacher = teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);
        if (findTeacher == null) {
            return "Error! Teacher with id " + id + " not found";
        }
        if (percent == null || percent <= 0 || percent > 100) {
            return "Error! Invalid percent";
        }
        Double salary = findTeacher.getSalary();
        if (salary == null) {
            return "Error! Teacher salary is not set";
        }
        if (salary < 0 || salary > 100000) {
            return "Error! Current salary is invalid";
        }
        Double newSalary = salary + (salary * (percent / 100.0));
        if (newSalary > 100000) {
            return "Fail! Salary is too big";
        }
        findTeacher.setSalary(newSalary);
        return "success";
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteById(@PathVariable Integer id) {
        if (teachers == null) {
            return false;
        }
        if (id == null || id < 0) {
            return false;
        }
//        Teacher findTeacher=teachers.stream().filter(teacher -> id.equals(teacher.getId())).findFirst().orElse(null);
//        if (findTeacher == null) {
//            return false;
//        }

        return teachers.removeIf(teacher -> id.equals(teacher.getId()));


//      teachers.remove(findTeacher);
//        return true;

    }


    @DeleteMapping("/delete-by-subject/{subject}")
    public Integer deleteBySubject(@PathVariable String subject) {
        if (teachers == null) {
            return 0;
        }
        if (subject == null || subject.isEmpty()) {
            return 0;
        }


       Integer count= Math.toIntExact(teachers.stream().filter(teacher -> teacher.getSubject()!=null&&subject.equalsIgnoreCase(teacher.getSubject())).count());

        teachers.removeIf(teacher ->teacher.getSubject()!=null&& subject.equalsIgnoreCase(teacher.getSubject()));


        return count;
    }

    @DeleteMapping("/delete-inactive")
    public Integer deleteInactive(){
        if (teachers == null) {
            return 0;
        }
        List<Teacher> deleteTeachers=teachers.stream().filter(teacher -> teacher.getActive() != null && teacher.getActive().equals(false)).toList();

        teachers.removeAll(deleteTeachers);

        return deleteTeachers.size();
    }
}






















