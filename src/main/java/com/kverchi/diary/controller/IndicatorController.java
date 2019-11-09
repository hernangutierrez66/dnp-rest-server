package com.kverchi.diary.controller;

import com.kverchi.diary.model.entity.Indicator;
import com.kverchi.diary.model.entity.IndicatorOrientation;
import com.kverchi.diary.model.entity.LogActivity;
import com.kverchi.diary.model.entity.User;
import com.kverchi.diary.repository.IndicatorOrientationRepository;
import com.kverchi.diary.repository.IndicatorRepository;

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
@RequestMapping("/indicator")
public class IndicatorController extends ValidatedController {

    private final IndicatorRepository indicatorRepository;
    private final IndicatorOrientationRepository indicatorOrientationRepository;
    private final UserRepository userRepository;
    private final LogActivityRepository logActivityRepository;

    @Autowired
    public IndicatorController(IndicatorRepository indicatorRepository, UserRepository userRepository, LogActivityRepository logActivityRepository,IndicatorOrientationRepository indicatorOrientationRepository) {
        this.indicatorRepository = indicatorRepository;
        this.indicatorOrientationRepository = indicatorOrientationRepository;
        this.userRepository = userRepository;
        this.logActivityRepository = logActivityRepository;
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

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Indicator", userid, "Crear", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_CREATION, HttpStatus.OK);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity getIndicator(@PathVariable(value = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(indicatorRepository.findById(id));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity updateIndicator(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id) throws ParseException {
        if (input == null || id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        Optional<Indicator> indicatorOptional = indicatorRepository.findById(id);
        if (!indicatorOptional.isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        Indicator indicator1 = indicatorOptional.get();

        indicator1.setName(input.get("name"));
        indicator1.setUnity(Integer.parseInt(input.get("unity")));
        if (!input.get("indicatorOrientation").isEmpty()){
            indicator1.setIndicatorOrientation(indicatorOrientationRepository.getOne(Integer.parseInt(input.get("indicatorOrientation"))));
        }
        indicatorRepository.save(indicator1);

        User user = userRepository.findByUserIdAndIsEnabled(Integer.parseInt(input.get("user_id")), true);
        if (user == null) return HierarchyController.customMessage("Usuario no autorizado", HttpStatus.BAD_REQUEST);
        int city = 0;
        if (user.getMunicipality() != null){
            city = user.getMunicipality().getId();
        }

        int userid = user.getUserId();

        LogActivity logActivity = new LogActivity("Indicator", userid, "Actualizar", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_UPDATE, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteIndicator(@RequestBody Map<String, String> input, @PathVariable(value = "id") Integer id){
        if (id == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        //indicatorRepository.deleteById(id);
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

        LogActivity logActivity = new LogActivity("Indicator", userid, "Update estado", city, ZonedDateTime.now());
        logActivityRepository.save(logActivity);

        return HierarchyController.customMessage(HierarchyController.SUCCESFUL_DELETE, HttpStatus.OK);
    }


}
