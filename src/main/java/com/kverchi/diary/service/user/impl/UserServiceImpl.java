package com.kverchi.diary.service.user.impl;

import com.kverchi.diary.model.Email;
import com.kverchi.diary.model.entity.*;
import com.kverchi.diary.repository.*;
import com.kverchi.diary.service.email.EmailService;
import com.kverchi.diary.service.email.impl.EmailTemplate;
import com.kverchi.diary.model.form.RegistrationForm;
import com.kverchi.diary.service.email.EmailMessagingProducerService;
import com.kverchi.diary.service.security.SecurityService;
import com.kverchi.diary.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String TOKEN_INVALID = "invalidToken";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_VALID = "valid";
    public static final String RESET_PASSWORD_OK = "Se ha enviado un correo electrónico.";

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    SecurityService securityService;

    @Autowired
    EmailService emailMessagingProducerService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MunicipalityRepository municipalityRepository;




    @Override
    public ServiceResponse login(User requestUser) {
        UsernamePasswordAuthenticationToken authenticationTokenRequest = new
                UsernamePasswordAuthenticationToken(requestUser.getUsername(), requestUser.getPassword());
        try {
            Authentication authentication = this.authenticationManager.authenticate(authenticationTokenRequest);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            /*HttpSession session = httpServletRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);*/

           // User user = (User)authentication.getPrincipal();
            logger.info("Logged in user: {}", authentication.getPrincipal());
            return new ServiceResponse(HttpStatus.OK, MsgServiceResponse.OK);

        } catch (BadCredentialsException ex) {
            return new ServiceResponse(HttpStatus.BAD_REQUEST, MsgServiceResponse.NO_USER_WITH_USERNAME_OR_PASSWORD_WRONG);
        }
    }

    @Override
    public ServiceResponse logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(
                    httpServletRequest,
                    httpServletResponse,
                    authentication);
        }
        return new ServiceResponse(HttpStatus.OK, MsgServiceResponse.OK);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public ServiceResponse register(Map<String, String> body) {
        ServiceResponse response = new ServiceResponse();
        if(!body.get("password").equals(body.get("matchingPassword"))) {
            response.setResponseMessage(MsgServiceResponse.NEW_PASSWORD_MISMATCHED);
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            return response;
        }


        Role role = roleRepository.findByRole(body.get("role"));

        Optional<Municipality> optionalMunicipality =  municipalityRepository.findById(Integer.parseInt(body.get("municipio_id")));

        User user = new User(body.get("username"), body.get("password"), false, body.get("email"), ZonedDateTime.now(),  Arrays.asList(role), optionalMunicipality.get());

                //form.toUser(bCryptPasswordEncoder);
        if(userRepository.findByUsername(user.getUsername()) != null) {
            response.setResponseMessage(MsgServiceResponse.USER_USERNAME_ALREADY_EXIST);
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            return response;
        }
        if(userRepository.findByEmail(user.getEmail()) != null) {
            response.setResponseMessage(MsgServiceResponse.USER_EMAIL_ALREADY_EXIST);
            response.setResponseCode(HttpStatus.BAD_REQUEST);
            return response;
        }

        userRepository.save(user);

        try {

            String securityToken =  securityService.generateSecurityToken();
            createVerificationTokenForUser(user,securityToken);

            String baseUrl = UserService.generateServerBaseUrl(httpServletRequest);
            String confirmLink = baseUrl + "/user/confirm/" + securityToken;

            List<String> recipientsAddress = Arrays.asList(user.getEmail());
            Map<String, Object> textVariables = new HashMap<>();
            textVariables.put("confirmEmailLink", confirmLink);
            Email registrationEmail = new Email(EmailTemplate.REGISTRATION_EMAIL, recipientsAddress, textVariables);
            emailMessagingProducerService.sendEmail(registrationEmail);
            response.setSuccessResponse();
        } catch (UnexpectedRollbackException e) {
            e.printStackTrace();
            response.setInternalServerErrorResponse();
        } catch (Exception e) {
            response.setInternalServerErrorResponse();
        }
        return response;
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                .getTime()) <= 0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        user.setEnabled(true);
        // tokenRepository.delete(verificationToken);
        userRepository.save(user);
        return TOKEN_VALID;
    }


    @Override
    public void activateAccount(User user) {

    }

    @Override
    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID()
                .toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public void createPasswordResetTokenForUser(final User user, final String token) {
        ServiceResponse response = new ServiceResponse();
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);

        List<String> recipientsAddress = Arrays.asList(user.getEmail());
        Map<String, Object> textVariables = new HashMap<>();
        String baseUrl = UserService.generateServerBaseUrl(httpServletRequest);
        String confirmLink = baseUrl + "/user/changePassword?id="  + user.getUserId() + "&token=" + myToken.getToken();
        textVariables.put("resetLink", confirmLink);
        Email resetPassword = new Email(EmailTemplate.RESET_PASSWORD_EMAIL, recipientsAddress, textVariables);
        emailMessagingProducerService.sendEmail(resetPassword);

    }


    @Override
    public String validatePasswordResetToken(long id, String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null) || (passToken.getUser().getUserId() != id)) {
            return TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return TOKEN_EXPIRED;
        }

        final User user = passToken.getUser();
        final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority("CHANGE_PASSWORD_PRIVILEGE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    @Override
    public String validateSavePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if ((passToken == null)) {
            return TOKEN_INVALID;
        }

        final Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return TOKEN_EXPIRED;
        }

        return null;
    }

    @Override
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }


    @Override
    public boolean updatePassword(User user) {
        return false;
    }

    @Override
    public boolean createAndSendResetPasswordToken(String email) {
        return false;
    }

    @Override
    public User getResetPasswordToken(String token) {
        return null;
    }

    @Override
    public User getUserFromSession() {
        User currentUser = new User();
        Object principal = securityService.getUserFromSession();
        if (principal instanceof User) {
            currentUser = (User) principal;
            return currentUser;
        }
        return currentUser;
    }

    @Override
    public boolean isValuePresent(String key, Object value) {
        return false;
    }

    @Override
    public void saveUserInfo(int userId, String info) {

    }

    @Override
    public boolean verifyPassword(String rawPass, String encodedPass) {
        return false;
    }

    @Override
    public User findUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findUserByTokenPassword(final String token) {
      PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
      return passwordResetToken.getUser();
    }
}
