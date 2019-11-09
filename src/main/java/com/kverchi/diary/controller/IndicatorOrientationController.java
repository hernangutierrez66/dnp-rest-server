package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.IndicatorOrientation;
import com.kverchi.diary.model.entity.LogActivity;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.repository.IndicatorOrientationRepository;

import com.kverchi.diary.repository.LogActivityRepository;
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
@RequestMapping("/indicator-orientation")
public class IndicatorOrientationController extends ValidatedController{

    private final IndicatorOrientationRepository indicatorOrientationRepository;
    private final UserRepository userRepository;
    private final LogActivityRepository logActivityRepository;

    @Autowired
    public IndicatorOrientationController(IndicatorOrientationRepository indicatorOrientationRepository, UserRepository userRepository, LogActivityRepository logActivityRepository) {
        this.indicatorOrientationRepository = indicatorOrientationRepository;
        this.userRepository = userRepository;
        this.logActivityRepository = logActivityRepository;
    }

    @GetMapping(value = "/all")
    public ResponseEntity listOrientations() {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorOrientationRepository.findAll());
    }

    @PostMapping(value = "/create")
    public ResponseEntity createIndicatorOrientation(@Valid @RequestBody Map<String, String> input) {
        if (input == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        IndicatorOrientation indicatorOrientation = new IndicatorOrientation();
        indicatorOrientation.setName(input.get("name"));
        indicatorOrientation.setState(1);
        indicatorOrientationRepository.save(indicatorOrientation);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("orientacion_indicador", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getOrientation(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorOrientationRepository.findById(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateIndicatorOrientation(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<IndicatorOrientation> indicatorOrientationOptional = indicatorOrientationRepository.findById(id);
        if (!indicatorOrientationOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        IndicatorOrientation orientation = indicatorOrientationOptional.get();

        orientation.setName(input.get("name"));
        indicatorOrientationRepository.save(orientation);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("orientacion_indicador", userid, "Actualizar", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteIndicatorOrientation(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        //indicatorOrientationRepository.deleteById(id);
        IndicatorOrientation indicatorOrientation = indicatorOrientationRepository.getOne(id);
        indicatorOrientation.setState(0);
        indicatorOrientationRepository.save(indicatorOrientation);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }
        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("orientacion_indicador", userid, "Actualizar, estado", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);
        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }
}
