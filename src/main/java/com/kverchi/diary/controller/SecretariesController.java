package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.*;
import com.kverchi.diary.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Map;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/secretaries")
public class SecretariesController {

    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;
    private final ResponsibleRepository responsibleRepository;
    private final LogActivityRepository logActivityRepository;
    private final ResponsibleTypeRepository responsibleTypeRepository;

    private final UserRepository userRepository;

    @Autowired
    public SecretariesController(DepartmentRepository departmentRepository, MunicipalityRepository municipalityRepository,
                                 ResponsibleRepository responsibleRepository, LogActivityRepository logActivityRepository,
                                 UserRepository userRepository, ResponsibleTypeRepository responsibleTypeRepository) {
        this.departmentRepository = departmentRepository;
        this.municipalityRepository = municipalityRepository;
        this.responsibleRepository = responsibleRepository;
        this.logActivityRepository = logActivityRepository;
        this.userRepository = userRepository;
        this.responsibleTypeRepository = responsibleTypeRepository;
    }

    @GetMapping(value = "/departments")
    public ResponseEntity listDepartments() {
        return ResponseEntity.status(HttpStatus.OK).body(departmentRepository.findAll());
    }

    @GetMapping(value = "/municipalities")
    public ResponseEntity listMunicipalities() {
        return ResponseEntity.status(HttpStatus.OK).body(municipalityRepository.findAll());
    }

    @GetMapping(value = "/responsibles")
    public ResponseEntity listResponsibles() {
        return ResponseEntity.status(HttpStatus.OK).body(responsibleRepository.findAll());
    }

    @GetMapping(value = "/department/{id}")
    public ResponseEntity getDepartment(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(departmentRepository.findById(id));
    }

    @GetMapping(value = "/municipality/{id}")
    public ResponseEntity getMunicipality(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(municipalityRepository.findById(id));
    }

    @GetMapping(value = "/responsible/{id}")
    public ResponseEntity getResponsible(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(responsibleRepository.findById(id));
    }

    @GetMapping(value = "/responsibleType/all")
    public ResponseEntity getResponsibleTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(responsibleTypeRepository.findAll());
    }

    @PostMapping(value = "/department")
    public ResponseEntity createDepartment(@Valid @RequestBody Map<String, String> input) {
        if (input == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Department department = new Department();
        department.setName(input.get("name"));
        department.setDaneCode(Integer.parseInt(input.get("daneCode")));
        department.setState(1);
        departmentRepository.save(department);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Deparment", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PostMapping(value = "/municipality")
    public ResponseEntity createMunicipality(@RequestBody Map<String, String> input) {
        if (input.get("name").isEmpty() || input.get("departmentId").isEmpty() || input.get("daneCode").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Municipality municipality = new Municipality();
        municipality.setDaneCode(Integer.parseInt(input.get("daneCode")));
        municipality.setName(input.get("name"));
        municipality.setState(1);
        municipality.setDepartment(departmentRepository.getOne(Integer.parseInt(input.get("departmentId"))));
        municipalityRepository.save(municipality);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Municipality", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PostMapping(value = "/responsible")
    public ResponseEntity createSecretary(@RequestBody Map<String, String> input) {
        if (input.get("name").isEmpty() || input.get("nit").isEmpty() || input.get("municipalityId").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Responsible secretary = new Responsible();
        secretary.setMunicipality(municipalityRepository.getOne(Integer.parseInt(input.get("municipalityId"))));
        secretary.setNit(Integer.parseInt(input.get("nit")));
        secretary.setName(input.get("name"));
        secretary.setState(1);
        responsibleRepository.save(secretary);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Responsible", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

}
