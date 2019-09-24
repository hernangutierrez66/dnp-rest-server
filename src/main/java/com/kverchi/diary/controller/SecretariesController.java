package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.Department;
import com.kverchi.diary.model.entity.Municipality;
import com.kverchi.diary.model.entity.Responsible;
import com.kverchi.diary.repository.DepartmentRepository;
import com.kverchi.diary.repository.MunicipalityRepository;
import com.kverchi.diary.repository.ResponsibleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.Map;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/secretaries")
public class SecretariesController {

    private final DepartmentRepository departmentRepository;
    private final MunicipalityRepository municipalityRepository;
    private final ResponsibleRepository responsibleRepository;

    @Autowired
    public SecretariesController(DepartmentRepository departmentRepository, MunicipalityRepository municipalityRepository, ResponsibleRepository responsibleRepository) {
        this.departmentRepository = departmentRepository;
        this.municipalityRepository = municipalityRepository;
        this.responsibleRepository = responsibleRepository;
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

    @PostMapping(value = "/department")
    public ResponseEntity createDepartment(@Valid @RequestBody Department department) {
        if (department == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        departmentRepository.save(department);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PostMapping(value = "/municipality")
    public ResponseEntity createMunicipality(@RequestBody Map<String, String> input) {
        if (input.get("name").isEmpty() || input.get("departmentId").isEmpty() || input.get("daneCode").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Municipality municipality = new Municipality();
        municipality.setDaneCode(Integer.parseInt(input.get("daneCode")));
        municipality.setName(input.get("name"));
        municipality.setDepartment(departmentRepository.getOne(Integer.parseInt(input.get("departmentId"))));
        municipalityRepository.save(municipality);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PostMapping(value = "/responsible")
    public ResponseEntity createSecretary(@RequestBody Map<String, String> input) {
        if (input.get("name").isEmpty() || input.get("nit").isEmpty() || input.get("municipalityId").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Responsible secretary = new Responsible();
        secretary.setMunicipality(municipalityRepository.getOne(Integer.parseInt(input.get("municipalityId"))));
        secretary.setNit(Integer.parseInt(input.get("nit")));
        secretary.setName(input.get("name"));
        responsibleRepository.save(secretary);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

}
