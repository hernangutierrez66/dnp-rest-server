package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.Indicator;
import com.kverchi.diary.repository.IndicatorOrientationRepository;
import com.kverchi.diary.repository.IndicatorRepository;

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
@RequestMapping("/indicator")
public class IndicatorController extends ValidatedController {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorOrientationRepository indicatorOrientationRepository;

    @Autowired
    public IndicatorController(IndicatorRepository indicatorRepository, IndicatorOrientationRepository indicatorOrientationRepository) {
        this.indicatorRepository = indicatorRepository;
        this.indicatorOrientationRepository = indicatorOrientationRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listIndicators() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createIndicatorOrientation(@Valid @RequestBody Map<String, String> input) {

        if (input.get("name").isEmpty() || input.get("unity").isEmpty() || input.get("orientation_indicatorid").isEmpty()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Indicator indicatorNuevo = new Indicator();
        indicatorNuevo.setName(input.get("name"));
        indicatorNuevo.setUnity(Integer.parseInt(input.get("unity")));
        indicatorNuevo.setIndicatorOrientation(indicatorOrientationRepository.getOne(Integer.parseInt(input.get("orientation_indicatorid"))));
        indicatorRepository.save(indicatorNuevo);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getIndicator(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findById(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateIndicator(@RequestBody Indicator indicator, @PathVariable(value = "id") Integer id) throws ParseException {
        if (indicator == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<Indicator> indicatorOptional = indicatorRepository.findById(id);
        if (!indicatorOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Indicator indicator1 = indicatorOptional.get();

        indicator1.setName(indicator.getName());
        indicator1.setUnity(indicator.getUnity());
        indicator1.setIndicatorOrientation(indicator.getIndicatorOrientation());
        indicatorRepository.save(indicator1);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteIndicator(@PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        indicatorRepository.deleteById(id);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }


}
