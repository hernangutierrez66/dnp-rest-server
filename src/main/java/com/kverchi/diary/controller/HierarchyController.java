package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.Hierarchy;
import com.kverchi.diary.model.entity.LogActivity;
import com.kverchi.diary.model.entity.Objective;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.repository.HierarchyRepository;
import com.kverchi.diary.repository.HierarchyTypeRepository;


import com.kverchi.diary.repository.LogActivityRepository;
import com.kverchi.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/hierarchy")
public class HierarchyController extends ValidatedController {

    private final HierarchyRepository repository;

    private final HierarchyTypeRepository hierarchyTypeRepository;

    private final LogActivityRepository logActivityRepository;

    private final UserRepository userRepository;

    public static final String SUCCESFUL_CREATION = "Creación Exitosa";

    public static final String SUCCESFUL_UPDATE = "Actualización Exitosa";

    public static final String SUCCESFUL_DELETE = "Eliminación Exitosa";

    public static final String NOT_FOUND = "No se encontró el elemento";


    @Autowired
    public HierarchyController(HierarchyRepository repository, HierarchyTypeRepository hierarchyTypeRepository,
                               LogActivityRepository logActivityRepository,
                               UserRepository userRepository) {
        this.repository = repository;
        this.hierarchyTypeRepository = hierarchyTypeRepository;
        this.logActivityRepository = logActivityRepository;
        this.userRepository = userRepository;
    }

    @PostMapping()
    public ResponseEntity createPlan(@Valid @RequestBody Map<String, String> input, @RequestParam(required = false) Integer parentId, @RequestParam Integer hierarchyType) {
        Hierarchy hierarchy = new Hierarchy();
        try {
            if (input.get("userid").isEmpty() || input.get("name").isEmpty() || input.get("periodoid").isEmpty() || hierarchyType == null) HierarchyController.customMessage("Completar los campos requeridos", HttpStatus.BAD_REQUEST);
            hierarchy.setStartDate(Hierarchy.DATE_FORMAT.parse(input.get("startDate")));
            hierarchy.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("endDate")));
            hierarchy.setOpen(true);
            if (hierarchyType != 1) hierarchy.setParent(repository.getOne(parentId));
            hierarchy.setType(hierarchyTypeRepository.getOne(hierarchyType));
            hierarchy.setUserid(userRepository.getOne(Integer.parseInt(input.get("userid"))));
            repository.save(hierarchy);
            LogActivity logActivity = new LogActivity("Hierarchy", hierarchy.getUserid().getUserId(), "Crear", hierarchy.getUserid().getMunicipality().getId(), ZonedDateTime.now());
            logActivityRepository.save(logActivity);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //LogActivity logActivity = new LogActivity("Hierarchy")
        return customMessage(SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getHierarchy(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }

    @GetMapping(value = "/all")
    public ResponseEntity getAllHierarchies() {
       return ResponseEntity.status(HttpStatus.OK).body(repository.findByType(hierarchyTypeRepository.getOne(1)));
        //return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
    }

    @GetMapping(value = "/route/{id}")
    public ResponseEntity getHierarchyParent(@PathVariable(value = "id") Integer id) {
        if (!repository.findById(id).isPresent()) return customMessage(NOT_FOUND, HttpStatus.NOT_FOUND);
        Hierarchy head = repository.findById(id).get().getParent();
        List<Map<String, String>> route = new ArrayList<>();
        Map<String, String> map;
        while(head != null){
            map = new HashMap<>();
            map.put("id", head.getId().toString());
            map.put("name", head.getName());
            route.add(map);
            head = head.getParent();
        }
        return ResponseEntity.status(HttpStatus.OK).body(route);
    }

    // All list methods for every type of hierarchy

    @GetMapping(value = "/plan")
    public ResponseEntity listPlans() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findByType(hierarchyTypeRepository.getOne(1)));
    }

    @GetMapping(value = "/axis")
    public ResponseEntity listAxis() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findByType(hierarchyTypeRepository.getOne(2)));
    }

    @GetMapping(value = "/program")
    public ResponseEntity listPrograms() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findByType(hierarchyTypeRepository.getOne(3)));
    }

    @GetMapping(value = "/project")
    public ResponseEntity listProjects() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findByType(hierarchyTypeRepository.getOne(4)));
    }

    @GetMapping(value = "/plan/{id}/axis")
    public ResponseEntity listPlanAxis(@PathVariable(value = "id") Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(repository.findByParent(repository.findById(id).orElseThrow(EntityNotFoundException::new)));
        } catch (EntityNotFoundException e) {
            return customMessage(NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/plan/{id}/objectives")
    public ResponseEntity listPlanObjectives(@PathVariable(value = "id") Integer id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id).orElseThrow(EntityNotFoundException::new).getObjectives());
        } catch (EntityNotFoundException e) {
            return customMessage(NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }


    @PatchMapping
    public ResponseEntity updateStatus(@RequestParam Integer id, @RequestParam boolean newStatus) {
        try {
            Hierarchy hierarchy = repository.findById(id).orElseThrow(EntityNotFoundException::new);
            hierarchy.setOpen(newStatus);
            repository.save(hierarchy);
            LogActivity logActivity = new LogActivity("Hierarchy", hierarchy.getUserid().getUserId(), "Actualizar", hierarchy.getUserid().getMunicipality().getId(), ZonedDateTime.now());
            logActivityRepository.save(logActivity);
            return customMessage("Modificación exitosa", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return customMessage(NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    static ResponseEntity customMessage(String message, HttpStatus status) {
        Map<String, String> customResponse = new HashMap<>();
        customResponse.put("message", message);
        return ResponseEntity.status(status).body(customResponse);
    }

}
