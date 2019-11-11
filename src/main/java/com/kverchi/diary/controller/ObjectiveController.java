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

    private final AttributeDefinitionRepository attributeDefinitionRepository;

    private final AttributeDefinitionObjectiveTypeRepository attributeDefinitionObjectiveTypeRepository;

    private final ObjectiveTypeRepository objectiveTypeRepository;

    private final AttributeValueRepository attributeValueRepository;

    private final IndicatorRepository indicatorRepository;

    private final LogActivityRepository logActivityRepository;

    @Autowired
    public ObjectiveController(ObjectiveRepository repository, HierarchyRepository hierarchyRepository,
                               TraceChangeValueRepository traceChangeValueRepository, AnnexesRepository annexesRepository,
                               NotificationRepository notificationRepository, UserRepository userRepository,
                               MunicipalityRepository municipalityRepository,
                               AttributeDefinitionRepository attributeDefinitionRepository,
                               AttributeDefinitionObjectiveTypeRepository attributeDefinitionObjectiveTypeRepository,
                               ObjectiveTypeRepository objectiveTypeRepository, AttributeValueRepository attributeValueRepository,
                               LogActivityRepository logActivityRepository, IndicatorRepository indicatorRepository) {
        this.repository = repository;
        this.hierarchyRepository = hierarchyRepository;
        this.traceChangeValueRepository = traceChangeValueRepository;
        this.annexesRepository = annexesRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.municipalityRepository = municipalityRepository;
        this.attributeDefinitionRepository = attributeDefinitionRepository;
        this.attributeDefinitionObjectiveTypeRepository = attributeDefinitionObjectiveTypeRepository;
        this.objectiveTypeRepository = objectiveTypeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.indicatorRepository = indicatorRepository;
        this.logActivityRepository = logActivityRepository;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getObjective(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findById(id));
    }

    @GetMapping(value = "/all")
    public ResponseEntity getAllObjectives() {
        return ResponseEntity.status(HttpStatus.OK).body(repository.findAll());
    }

    @PostMapping()
    public ResponseEntity createObjective(@Valid @RequestBody Map<String,String> input, @RequestParam(required = false) Integer parentId) {
        if (input == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (input.get("name").isEmpty() || input.get("description").isEmpty() || input.get("expectedValue").isEmpty() || input.get("objectiveType").isEmpty() || input.get("indicator").isEmpty()) return HierarchyController.customMessage("Completar los campos requeridos", HttpStatus.BAD_REQUEST);
        Objective objective = new Objective();
        try {
            objective.setState(1);
            objective.setName(input.get("name"));
            objective.setStartDate(Hierarchy.DATE_FORMAT.parse(input.get("startDate")));
            objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("endDate")));
            objective.setDescription(input.get("description"));
            objective.setExpectedValue(Integer.parseInt(input.get("expectedValue")));
            objective.setActualValue(Integer.parseInt(input.get("actualValue")));
            objective.setObjectiveType(objectiveTypeRepository.getOne(Integer.parseInt(input.get("objectiveType"))));
            objective.setIndicator(indicatorRepository.getOne(Integer.parseInt(input.get("indicator"))));
            objective.setUserId(Integer.parseInt(input.get("user_id")));
            objective.setParent(repository.getOne(Integer.parseInt(input.get("parent"))));
            if (parentId != null){
                Hierarchy hierarchy = hierarchyRepository.getOne(parentId);
                objective.addObjective(hierarchy);
                hierarchy.addObjective(objective);
                hierarchyRepository.save(hierarchy);
            }
            repository.save(objective);

            User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
            if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
            int city = 0;
            if (user.getMunicipality() != null){
                city = user.getMunicipality().getId();
            }
            int userid = user.getUserId();
            LogActivity logActivity = new LogActivity("Objective", userid, "Crear", city, ZonedDateTime.now());
            logActivityRepository.save(logActivity);

        } catch (ParseException e) {
            e.printStackTrace();
        }
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

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Objective", userid, "Actualizar progreso", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

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

        int userid = user.getUserId();

        Optional<Objective> objectiveOptional = repository.findById(id);
        if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Objective objective = objectiveOptional.get();
        objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
        //objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
        repository.save(objective);
        TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, Integer.parseInt(input.get("newValue")), ZonedDateTime.now(), 1, null, null);
        traceChangeValueRepository.save(traceChangeValue);


        Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")), 1, ZonedDateTime.now());
        annexesRepository.save(annexes);

        Notification notification = new Notification("nombre justificacion",
                userid,
                input.get("justification"),
                input.get("aditional_information"),
                city,
                objective.getId(),
                "Contrato",
                Integer.parseInt(input.get("state_change")),
                1,
                Hierarchy.DATE_FORMAT.parse(input.get("startDate")),
                Hierarchy.DATE_FORMAT.parse(input.get("newDate")),
                false);
        notificationRepository.save(notification);

        LogActivity logActivity = new LogActivity("Objective", userid, "Update", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping (value = "/{id}/annexes")
    public ResponseEntity getObjectivesAnnexes (@PathVariable(value = "id") Integer id){
        if (id == null ) return HierarchyController.customMessage("Id requerido", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.OK).body(annexesRepository.findByTraceChangeValue(traceChangeValueRepository.getOne(id)));
    }

    @GetMapping (value = "/{id}/trace")
    public ResponseEntity getObjectivesTrace (@PathVariable(value = "id") Integer id){
        if (id == null ) return HierarchyController.customMessage("Id requerido", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.OK).body(traceChangeValueRepository.findByObjective(repository.getOne(id)));
    }

    @GetMapping (value = "/notifications/{userid}")
    public ResponseEntity getNotifications (@PathVariable(value = "userid") Integer id){
        if (id == null ) return HierarchyController.customMessage("Id requerido", HttpStatus.BAD_REQUEST);
        User user = userRepository.getOne(id);
        if (user.getRoles().equals("ROLE_JEFECONTROL")){
            return ResponseEntity.status(HttpStatus.OK).body(notificationRepository.findByNotifyControlBoss(true));
        }else if (user.getRoles().equals("ROLE_LIDERSEGUIMIENTO")){
            return ResponseEntity.status(HttpStatus.OK).body(notificationRepository.findByNotifyControlBoss(false));
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(notificationRepository.findAll());
        }

    }

    @PatchMapping(value = "/{id}/contract-state")
    public ResponseEntity suspendContract(@RequestBody Map<String,String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        if (input.get("state_change").isEmpty())  return HierarchyController.customMessage("El nuevo estado de contrato es requerido", HttpStatus.BAD_REQUEST);
        Optional<Objective> objectiveOptional = repository.findById(id);
        Objective objective = objectiveOptional.get();
        if (input.get("state_change").equals("2")){ //contrato suspendido temporalmente
            if (input.get("startDate" ) == "" || input.get("newDate") == "") return HierarchyController.customMessage("Fecha de inicio y fecha final son requeridos", HttpStatus.BAD_REQUEST);


            if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

            objective.setState(2);
            objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
            objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
            repository.save(objective);
            TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, Integer.parseInt(input.get("newValue")), ZonedDateTime.now(), 1, null, null);
            traceChangeValueRepository.save(traceChangeValue);
            Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")), 1, ZonedDateTime.now());
            annexesRepository.save(annexes);
        }

        if (input.get("state_change").equals("3")){ //contrato suspendido permanente
            // if (input.get("startDate" ) == "" || input.get("newDate") == "") return HierarchyController.customMessage("Fecha de inicio y fecha final son requeridos", HttpStatus.BAD_REQUEST);

            if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

            objective.setState(3);
            repository.save(objective);
            TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, objective.getActualValue(), ZonedDateTime.now(), 1, null, null);
            traceChangeValueRepository.save(traceChangeValue);
            Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse(ZonedDateTime.now().toString()), Hierarchy.DATE_FORMAT.parse("0000-00-00"), 1, ZonedDateTime.now());
            annexesRepository.save(annexes);
        }

        if (input.get("state_change").equals("1")){ //reinicio de contrato
            if (input.get("startDate" ) == "" || input.get("newDate") == "") return HierarchyController.customMessage("Fecha de inicio y fecha final son requeridos", HttpStatus.BAD_REQUEST);


            if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

            objective.setState(1);
            objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
            objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
            repository.save(objective);
            TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, Integer.parseInt(input.get("newValue")), ZonedDateTime.now(), 1, null, null);
            traceChangeValueRepository.save(traceChangeValue);
            Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")), 1, ZonedDateTime.now());
            annexesRepository.save(annexes);
            if (input.get("atributo_definicion_tipo_objetivoid")!= null){
                Optional<AttributeDefinitionObjectiveType> optionalAttributeDefinitionObjectiveType = attributeDefinitionObjectiveTypeRepository.findById(Integer.parseInt(input.get("atributo_definicion_tipo_objetivoid")));
                if (optionalAttributeDefinitionObjectiveType.isPresent()) return HierarchyController.customMessage("Definicion de atributo y tipo de objetivo inexistente", HttpStatus.BAD_REQUEST);
                AttributeValue attributeValue = new AttributeValue();
                attributeValue.setObjective(objective);
                attributeValue.setAttributeDefinitionObjectiveType(optionalAttributeDefinitionObjectiveType.get());
                attributeValueRepository.save(attributeValue);
            }
            //objeto de atributo valor
        }

        if (input.get("state_change").equals("0")){ //eliminacion de contrato
            //if (input.get("startDate" ) == "" || input.get("newDate") == "") return HierarchyController.customMessage("Fecha de inicio y fecha final son requeridos", HttpStatus.BAD_REQUEST);


            if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

            objective.setState(0);
            //objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
            //objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
            repository.save(objective);
            TraceChangeValue  traceChangeValue = new TraceChangeValue(objective, objective.getActualValue(), ZonedDateTime.now(), 1, null, null);
            traceChangeValueRepository.save(traceChangeValue);
            Annexes annexes = new Annexes(traceChangeValue, input.get("justification"), Hierarchy.DATE_FORMAT.parse("0000-00-00"), Hierarchy.DATE_FORMAT.parse("0000-00-00"), 1, ZonedDateTime.now());
            annexesRepository.save(annexes);
            //objeto de atributo valor
        }

        Optional<Notification> optionalNotification = notificationRepository.findById(Integer.parseInt(input.get("notification_id")));
        Notification notification = optionalNotification.get();

        notification.setState(0);
        notificationRepository.save(notification);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Objective", userid, "Actualizar, suspender", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/deny")
    public ResponseEntity denyDelete(@RequestBody Map<String,String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

       // if (input.get("notify_control_boss") == "")  return HierarchyController.customMessage("El nuevo estado de contrato es requerido", HttpStatus.BAD_REQUEST);
        Optional<Objective> objectiveOptional = repository.findById(id);
        Objective objective = objectiveOptional.get();


       // if (input.get("notify_control_boss") != "0"){ //rechazo de eliminacion de contrato
            if (input.get("startDate" ).isEmpty() || input.get("newDate").isEmpty()) return HierarchyController.customMessage("Fecha de inicio y fecha final son requeridos", HttpStatus.BAD_REQUEST);
            User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
            if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
            int city = 0;
            if (user.getMunicipality() != null){
                city = user.getMunicipality().getId();
            }

            if (!objectiveOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);

            objective.setState(1);
            objective.setEndDate(Hierarchy.DATE_FORMAT.parse(input.get("newDate")));
            objective.setDescription(objective.getDescription()+"\n"+input.get("justification"));
            repository.save(objective);

            Notification notification = new Notification("-", user.getUserId(), input.get("justification"), input.get("aditional_information"), city, objective.getId(), "Contrato", 1,1, Hierarchy.DATE_FORMAT.parse(input.get("startDate")), Hierarchy.DATE_FORMAT.parse(input.get("newDate")), true);
            notificationRepository.save(notification);
       // }

        Optional<Notification> optionalNotification1 = notificationRepository.findById(Integer.parseInt(input.get("notification_id")));
        Notification notification1 = optionalNotification1.get();

        notification.setState(0);
        notificationRepository.save(notification1);


        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Objective", userid, "Actualizar, negar eliminacion", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @PostMapping(value = "/create/attribute")
    public ResponseEntity createAttributeDefinition(@Valid @RequestBody Map<String,String> input) {
        if (input.get("nombre") == null || input.get("descripcion") == null || input.get("tipo") == null) return HierarchyController.customMessage("Nombre, descripcion y tipo son requeridos", HttpStatus.BAD_REQUEST);

        AttributeDefinition attributeDefinition = new AttributeDefinition(input.get("nombre"), input.get("descripcion"), Integer.parseInt(input.get("tipo")), 1);
        attributeDefinitionRepository.save(attributeDefinition);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Attribute definition", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping(value = "/attribute/all")
    public ResponseEntity getAllAttribute(){
        return ResponseEntity.status(HttpStatus.OK).body(attributeDefinitionRepository.findAll());
    }

    @GetMapping(value = "/{id}/attribute")
    public ResponseEntity getAttribute(@PathVariable(value = "id") Integer id) {
        // return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(attributeDefinitionRepository.findById(id));
    }

    @PostMapping(value = "/create/attributeObjectiveType")
    public ResponseEntity createAttributeDefinitionObjectiveType(@Valid @RequestBody Map<String,String> input) {
        if (input.get("atributo_definitionid") == null || input.get("tipo_objetivoid") == null) return HierarchyController.customMessage("Id de tipo de objetivo y definicion de atributo requerido", HttpStatus.BAD_REQUEST);

        Optional<AttributeDefinition> optionalAttributeDefinition = attributeDefinitionRepository.findById(Integer.parseInt(input.get("atributo_definitionid")));
        Optional<ObjectiveType> optionalObjectiveType = objectiveTypeRepository.findById(Integer.parseInt(input.get("tipo_objetivoid")));
        if (!optionalObjectiveType.isPresent()) return HierarchyController.customMessage("Tipo de objetivo no existe", HttpStatus.BAD_REQUEST);
        if (!optionalAttributeDefinition.isPresent()) return HierarchyController.customMessage("Definicion de atributo no existe", HttpStatus.BAD_REQUEST);
        AttributeDefinitionObjectiveType attributeDefinitionObjectiveType = new AttributeDefinitionObjectiveType();
        attributeDefinitionObjectiveType.setState(1);
        attributeDefinitionObjectiveType.setAttributeDefinition(optionalAttributeDefinition.get());
        attributeDefinitionObjectiveType.setObjectiveType(optionalObjectiveType.get());
        attributeDefinitionObjectiveTypeRepository.save(attributeDefinitionObjectiveType);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Pivot attribute definition Objective type", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping(value = "/attributeObjectiveType/all")
    public ResponseEntity getAllAttributeObjectiveType(){
        return ResponseEntity.status(HttpStatus.OK).body(attributeDefinitionObjectiveTypeRepository.findAll());
    }

    @GetMapping(value = "/{id}/attributeObjectiveType")
    public ResponseEntity getAttributeObjectiveType(@PathVariable(value = "id") Integer id) {

        return ResponseEntity.status(HttpStatus.OK).body(attributeDefinitionObjectiveTypeRepository.findById(id));
    }


}
