package com.kverchi.diary.controller;
import com.kverchi.diary.model.entity.Hierarchy;
import com.kverchi.diary.model.entity.Objective;
import com.kverchi.diary.repository.HierarchyRepository;
import com.kverchi.diary.repository.ObjectiveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/objectives")
public class ObjectiveController{

    private final ObjectiveRepository repository;

    private final HierarchyRepository hierarchyRepository;

    @Autowired
    public ObjectiveController(ObjectiveRepository repository, HierarchyRepository hierarchyRepository) {
        this.repository = repository;
        this.hierarchyRepository = hierarchyRepository;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getObjective(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }

    @PostMapping()
    public ResponseEntity createObjective(@Valid @RequestBody Objective objective, @RequestParam(required = false) Integer parentId) {
        if (objective == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (parentId != null){
            Hierarchy hierarchy = hierarchyRepository.getOne(parentId);
            objective.addObjective(hierarchy);
            hierarchy.addObjective(objective);
            hierarchyRepository.save(hierarchy);
        }
        repository.save(objective);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/progress")
    public ResponseEntity addProgress(@RequestBody Map<String,String> input, @PathVariable(value = "id") Integer id){
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<Objective> objectiveOptional = repository.findById(id);
        if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Objective objective = objectiveOptional.get();
        objective.setActualValue(objective.getActualValue()+Integer.parseInt(input.get("newValue")));
        objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
        repository.save(objective);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/date")
    public ResponseEntity changeDate(@RequestBody Map<String,String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<Objective> objectiveOptional = repository.findById(id);
        if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Objective objective = objectiveOptional.get();
        objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
        objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
        repository.save(objective);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

}
