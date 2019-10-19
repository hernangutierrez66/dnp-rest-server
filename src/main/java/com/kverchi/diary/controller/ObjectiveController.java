package com.kverchi.diary.controller;
import com.kverchi.diary.model.entity.*;
import com.kverchi.diary.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/objectives")
public class ObjectiveController{

    private final ObjectiveRepository repository;

    private final HierarchyRepository hierarchyRepository;

    private final TraceChangeValueRepository traceChangeValueRepository;

    private final AnnexesRepository annexesRepository;

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final MunicipalityRepository municipalityRepository;

    @Autowired
    public ObjectiveController(ObjectiveRepository repository, HierarchyRepository hierarchyRepository,
                               TraceChangeValueRepository traceChangeValueRepository, AnnexesRepository annexesRepository,
                               NotificationRepository notificationRepository, UserRepository userRepository, MunicipalityRepository municipalityRepository) {
        this.repository = repository;
        this.hierarchyRepository = hierarchyRepository;
        this.traceChangeValueRepository = traceChangeValueRepository;
        this.annexesRepository = annexesRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.municipalityRepository = municipalityRepository;
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
        TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, Integer.parseInt(input.get("newValue")), ZonedDateTime.now(), 1, ZonedDateTime.now(), null);
        traceChangeValueRepository.save(traceChangeValue);


        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/date")
    public ResponseEntity changeDate(@RequestBody Map<String,String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (input.get("startDate" ) == "" || input.get("newDate") == "" )  return HierarchyController.customMessage("Fecha de inicio y fecha final requerido", HttpStatus.BAD_REQUEST);
        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        Optional<Objective> objectiveOptional = repository.findById(id);
        if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Objective objective = objectiveOptional.get();
        objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
        objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
        repository.save(objective);
        TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, Integer.parseInt(input.get("newValue")), ZonedDateTime.now(), 1, null, null);
        traceChangeValueRepository.save(traceChangeValue);


        Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")), 1, ZonedDateTime.now());
        annexesRepository.save(annexes);

        Notification notification = new Notification("nombre justificacion", user.getUserId(), input.get("justification"), city, objective.getId(), "Contrato", 3,1, Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
        notificationRepository.save(notification);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }



}
