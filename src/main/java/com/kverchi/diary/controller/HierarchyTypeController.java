package com.kverchi.diary.controller;


import com.kverchi.diary.model.entity.HierarchyType;
import com.kverchi.diary.model.entity.LogActivity;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.repository.*;

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
@RequestMapping("/hierarchy-type")
public class HierarchyTypeController extends ValidatedController {

    private final HierarchyTypeRepository hierarchyTypeRepository;
    private final MunicipalityRepository municipalityRepository;
    private final ResponsibleTypeRepository responsibleTypeRepository;
    private final UserRepository userRepository;
    private final LogActivityRepository logActivityRepository;

    @Autowired
    public HierarchyTypeController(HierarchyTypeRepository hierarchyTypeRepository, MunicipalityRepository municipalityRepository,
                                   ResponsibleTypeRepository responsibleTypeRepository, UserRepository userRepository, LogActivityRepository logActivityRepository) {
        this.hierarchyTypeRepository = hierarchyTypeRepository;
        this.municipalityRepository = municipalityRepository;
        this.responsibleTypeRepository = responsibleTypeRepository;
        this.userRepository = userRepository;
        this.logActivityRepository = logActivityRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listHierarchyType() {
        return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createHierarchyType(@Valid @RequestBody Map<String, String> input) {

        if (input.get("name").isEmpty() || input.get("municipio_id").isEmpty() ||  input.get("tipo_responsableid").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HierarchyType hierarchyType = new HierarchyType();
        hierarchyType.setName(input.get("name"));
        hierarchyType.setMunicipality(municipalityRepository.getOne(Integer.parseInt(input.get("municipio_id"))));
        if (!input.get("tipo_jerarquia_id").isEmpty()){
            hierarchyType.setType(hierarchyTypeRepository.getOne(Integer.parseInt(input.get("tipo_jerarquia_id"))));
        }
        hierarchyType.setState(1);
        hierarchyType.setResponsibleType(responsibleTypeRepository.getOne(Integer.parseInt(input.get("tipo_responsableid"))));
        hierarchyTypeRepository.save(hierarchyType);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Hierarchy_type", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getHierarchyType(@PathVariable(value = "id") Integer id) {
       // return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findByState(id));
    }

    @GetMapping(value = "/city/{id}")
    public ResponseEntity getHierarchyTypeByCity(@PathVariable(value = "id") Integer id) {
        // return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findById(id));
        return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findByMunicipality(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateHierarchyType(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<HierarchyType> hierarchyTypeOptional = hierarchyTypeRepository.findById(id);
        if (!hierarchyTypeOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        HierarchyType hierarchyType1 = hierarchyTypeOptional.get();
        hierarchyType1.setName(input.get("name"));
        hierarchyType1.setMunicipality(municipalityRepository.getOne(Integer.parseInt(input.get("municipality"))));
        hierarchyType1.setType(hierarchyTypeRepository.getOne(Integer.parseInt(input.get("type"))));
        hierarchyType1.setResponsibleType(responsibleTypeRepository.getOne(Integer.parseInt(input.get("responsibleType"))));
        hierarchyTypeRepository.save(hierarchyType1);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Hierarchy_type", userid, "Update", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteHierarchyType(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id){
        if (id == null || input == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        //hierarchyTypeRepository.deleteById(id);
        HierarchyType hierarchyType = hierarchyTypeRepository.getOne(id);
        hierarchyType.setState(0);
        hierarchyTypeRepository.save(hierarchyType);
        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Hierarchy_type", userid, "Actualizar estado", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }
}
