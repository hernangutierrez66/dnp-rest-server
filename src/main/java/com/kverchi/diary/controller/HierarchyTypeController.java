package com.kverchi.diary.controller;


import com.kverchi.diary.model.entity.HierarchyType;
import com.kverchi.diary.repository.HierarchyTypeRepository;
import com.kverchi.diary.repository.MunicipalityRepository;
import com.kverchi.diary.repository.ResponsibleTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/hierarchy-type")
public class HierarchyTypeController extends ValidatedController {

    private final HierarchyTypeRepository hierarchyTypeRepository;
    private final MunicipalityRepository municipalityRepository;
    private final ResponsibleTypeRepository responsibleTypeRepository;

    @Autowired
    public HierarchyTypeController(HierarchyTypeRepository hierarchyTypeRepository, MunicipalityRepository municipalityRepository, ResponsibleTypeRepository responsibleTypeRepository) {
        this.hierarchyTypeRepository = hierarchyTypeRepository;
        this.municipalityRepository = municipalityRepository;
        this.responsibleTypeRepository = responsibleTypeRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listHierarchyType() {
        return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createHierarchyType(@Valid @RequestBody Map<String, String> input) {

        if (input.get("name").isEmpty() || input.get("municipio_id").isEmpty() || input.get("tipo_jerarquia_id").isEmpty() || input.get("tipo_responsableid").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        HierarchyType hierarchyType = new HierarchyType();
        hierarchyType.setName(input.get("name"));
        hierarchyType.setMunicipality(municipalityRepository.getOne(Integer.parseInt(input.get("municipio_id"))));
        hierarchyType.setType(hierarchyTypeRepository.getOne(Integer.parseInt(input.get("tipo_jerarquia_id"))));
        hierarchyType.setResponsibleType(responsibleTypeRepository.getOne(Integer.parseInt(input.get("tipo_responsableid"))));
        hierarchyTypeRepository.save(hierarchyType);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getHierarchyType(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(hierarchyTypeRepository.findById(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateHierarchyType(@RequestBody HierarchyType hierarchyType, @PathVariable(value = "id") Integer id) throws ParseException {
        if (hierarchyType == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<HierarchyType> hierarchyTypeOptional = hierarchyTypeRepository.findById(id);
        if (!hierarchyTypeOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        HierarchyType hierarchyType1 = hierarchyTypeOptional.get();
        hierarchyType1.setName(hierarchyType.getName());
        hierarchyType.setMunicipality(hierarchyType.getMunicipality());
        hierarchyType.setType(hierarchyType.getType());
        hierarchyType.setResponsibleType(hierarchyType.getResponsibleType());
        hierarchyTypeRepository.save(hierarchyType1);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteHierarchyType(@PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        hierarchyTypeRepository.deleteById(id);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }
}
