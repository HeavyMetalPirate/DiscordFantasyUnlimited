package com.fantasyunlimited.rest;

import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.enums.UserFoundStatus;
import com.fantasyunlimited.data.service.FantasyUnlimitedUserService;
import com.fantasyunlimited.rest.dto.UserRegistrationBody;
import com.fantasyunlimited.rest.dto.UserRegistrationResponse;
import com.fantasyunlimited.rest.dto.UserRegistrationStatus;
import com.fantasyunlimited.rest.dto.UserSessionInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequestMapping("/api/user")
public class UserManagementController {
    private static final Logger log = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private FantasyUnlimitedUserService userService;

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public ResponseEntity<UserSessionInformation> currentUserNameSimple(HttpServletRequest request, CsrfToken token) {
        UserSessionInformation sessionInformation = new UserSessionInformation(request.getUserPrincipal(), token);
        return new ResponseEntity<>(sessionInformation, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationBody requestBody) {
        log.info("Got RegistrationRequest: {}", requestBody);
        try {
            UserFoundStatus status = userService.userExists(requestBody.getUsername(), requestBody.getEmail());

            if (status == UserFoundStatus.BY_NAME) {
                return new ResponseEntity<>(new UserRegistrationResponse("Username exists already!", UserRegistrationStatus.USERNAME_FOUND),
                        HttpStatus.NOT_ACCEPTABLE);
            } else if (status == UserFoundStatus.BY_EMAIL) {
                return new ResponseEntity<>(new UserRegistrationResponse("E-Mail exists already!", UserRegistrationStatus.EMAIL_FOUND),
                        HttpStatus.NOT_ACCEPTABLE);
            }

            FantasyUnlimitedUser user = new FantasyUnlimitedUser();
            user.setUserName(requestBody.getUsername());
            user.setUserEmail(requestBody.getEmail());
            user.setPassword(requestBody.getPassword());
            user.setRole("USER");
            userService.createUser(user);

            return new ResponseEntity<>(new UserRegistrationResponse("Registration successful!", UserRegistrationStatus.REGISTERED),
                    HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("Error registering user.", e);
            return new ResponseEntity<>(new UserRegistrationResponse(e.getMessage(), UserRegistrationStatus.INTERNAL_ERROR),
                    HttpStatus.OK);
        }
    }


}
