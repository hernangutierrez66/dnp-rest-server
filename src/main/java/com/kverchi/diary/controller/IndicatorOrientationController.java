package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.IndicatorOrientation;
import com.kverchi.diary.repository.IndicatorOrientationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.text.ParseException;
import java.util.Optional;

@RestController
@EnableJpaRepositories("com.kverchi.diary.repository")
@RequestMapping("/indicator-orientation")
public class IndicatorOrientationController extends ValidatedController{

    private final IndicatorOrientationRepository indicatorOrientationRepository;

    @Autowired
    public IndicatorOrientationController(IndicatorOrientationRepository indicatorOrientationRepository) {
        this.indicatorOrientationRepository = indicatorOrientationRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listOrientations() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorOrientationRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createIndicatorOrientation(@Valid @RequestBody IndicatorOrientation indicatorOrientation) {
        if (indicatorOrientation == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        indicatorOrientationRepository.save(indicatorOrientation);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getOrientation(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorOrientationRepository.findById(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateIndicatorOrientation(@RequestBody IndicatorOrientation indicatorOrientation, @PathVariable(value = "id") Integer id) throws ParseException {
        if (indicatorOrientation == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<IndicatorOrientation> indicatorOrientationOptional = indicatorOrientationRepository.findById(id);
        if (!indicatorOrientationOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        IndicatorOrientation orientation = indicatorOrientationOptional.get();

        orientation.setName(indicatorOrientation.getName());
        indicatorOrientationRepository.save(orientation);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteIndicatorOrientation(@PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        indicatorOrientationRepository.deleteById(id);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }
}
