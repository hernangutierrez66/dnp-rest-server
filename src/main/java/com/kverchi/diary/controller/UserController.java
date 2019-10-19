package com.kverchi.diary.controller;

import com.kverchi.diary.service.user.impl.MsgServiceResponse;
import com.kverchi.diary.service.user.impl.ServiceResponse;
import com.kverchi.diary.model.form.RegistrationForm;
import com.kverchi.diary.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.kverchi.diary.model.entity.User;
import org.springframework.web.servlet.view.RedirectView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    @Autowired
    HttpServletResponse httpServletResponse;

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @PostMapping(value = "/login")
    @ResponseBody
    public ServiceResponse processLogin(@RequestBody User requestUser) {
        ServiceResponse response = userService.login(requestUser);
        return response;
    }
    @GetMapping(value = "/logout")
    @ResponseBody
    public ServiceResponse processLogout() {
        ServiceResponse response = userService.logout();
        return response;
    }
    @PostMapping(value = "/register")
    @ResponseBody
    public ServiceResponse processRegistration(@RequestBody Map<String, String> body) {
        ServiceResponse response = userService.register(body);
        return response;
    }

    @GetMapping(value = "/confirm/{securityToken}")
    public RedirectView confirmRegistration(@PathVariable("securityToken") String securityToken) {
        logger.info("Security token from email: " + securityToken);
        userService.validateVerificationToken(securityToken);

        return new RedirectView("http://localhost:4200/login");
    }


    // Reset password
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public void resetPassword(@RequestBody Map<String, String> email) {
        final User user = userService.findUserByEmail(email.get("email"));

        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
           //mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));

        }

    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse changePassword(@RequestParam("id") final long id, @RequestParam("token") final String token) {
        final String result = userService.validatePasswordResetToken(id, token);
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setResponseCode(HttpStatus.OK);
        if (result != null) {
           // model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
            serviceResponse.setResponseCode(HttpStatus.BAD_REQUEST);

        }

        serviceResponse.setResponseObject(token);
        return serviceResponse;
        //return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }

    @RequestMapping(value = "/savePassword", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse savePassword(@RequestBody Map<String, String> body) {
        String result = userService.validateSavePasswordResetToken(body.get("resetToken"));
        ServiceResponse response = new ServiceResponse();
        if (!body.get("password").equals(body.get("matchingPassword"))){
            response.setResponseMessage(MsgServiceResponse.NEW_PASSWORD_MISMATCHED);
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            return response;
        }
        if (result != null) {
           response.setResponseCode(HttpStatus.BAD_REQUEST);
           response.setResponseMessage(MsgServiceResponse.TOKEN_EXPIRED_OR_INVALID);

           return response;

        }
       // final User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findUserByTokenPassword(body.get("resetToken"));
        userService.changeUserPassword(user, body.get("password"));
        response.setResponseCode(HttpStatus.OK);
        return response;
    }

}
