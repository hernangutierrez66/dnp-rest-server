package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.HierarchyType;
import com.kverchi.diary.model.entity.LogActivity;
import com.kverchi.diary.model.entity.ObjectiveType;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.repository.HierarchyTypeRepository;
import com.kverchi.diary.repository.LogActivityRepository;
import com.kverchi.diary.repository.ObjectiveTypeRepository;

import com.kverchi.diary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/objective-type")
public class ObjectiveTypeController {

    private final ObjectiveTypeRepository objectiveTypeRepository;
    private  final HierarchyTypeRepository hierarchyTypeRepository;
    private final UserRepository userRepository;
    private final LogActivityRepository logActivityRepository;


    @Autowired
    public ObjectiveTypeController(ObjectiveTypeRepository objectiveTypeRepository, HierarchyTypeRepository hierarchyTypeRepository, UserRepository userRepository, LogActivityRepository logActivityRepository) {
        this.objectiveTypeRepository = objectiveTypeRepository;
        this.hierarchyTypeRepository = hierarchyTypeRepository;
        this.userRepository = userRepository;
        this.logActivityRepository = logActivityRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listObjectiveType() {
        return ResponseEntity.status(HttpStatus.OK).body(objectiveTypeRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createObjectiveType(@Valid @RequestBody Map<String, String> input) {

        if (input.get("name").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        ObjectiveType objectiveType = new ObjectiveType();
        if (!input.get("parentId").isEmpty()){
            HierarchyType hierarchyType = hierarchyTypeRepository.getOne(Integer.parseInt(input.get("parentId")));
            objectiveType.addObjective(hierarchyType);
            hierarchyType.addObjective(objectiveType);
            hierarchyTypeRepository.save(hierarchyType);
        }

        if (!input.get("tipo_padre_id").isEmpty()){
            objectiveType.setParent(objectiveTypeRepository.getOne(Integer.parseInt(input.get("tipo_padre_id"))));
        }
        objectiveType.setName(input.get("name"));
        objectiveTypeRepository.save(objectiveType);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("objective_type", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);

    }

   /* public ResponseEntity createObjectiveType(@Valid @RequestBody ObjectiveType objectiveType, @RequestParam(required = false) Integer parentId) {
        if (objectiveType == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (parentId != null){
            HierarchyType hierarchyType = hierarchyTypeRepository.getOne(parentId);
            objectiveType.addObjective(hierarchyType);
            hierarchyType.addObjective(objectiveType);
            hierarchyTypeRepository.save(hierarchyType);
        }
        objectiveTypeRepository.save(objectiveType);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }
*/
    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateObjectiveType(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<ObjectiveType> objectiveTypeOptional = objectiveTypeRepository.findById(id);
        if (!objectiveTypeOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        ObjectiveType objectiveType1 = objectiveTypeOptional.get();
        objectiveType1.setName(input.get("name"));
        objectiveType1.setParent(objectiveTypeRepository.getOne(Integer.parseInt(input.get("parent"))));
        objectiveTypeRepository.save(objectiveType1);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("objective_type", userid, "Actualizar", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getObjectiveType(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(objectiveTypeRepository.findById(id));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteObjectiveType(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        //objectiveTypeRepository.deleteById(id);
        ObjectiveType objectiveType = objectiveTypeRepository.getOne(id);
        objectiveType.setState(0);
        objectiveTypeRepository.save(objectiveType);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("objective_type", userid, "Actualizar estado", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }
}
