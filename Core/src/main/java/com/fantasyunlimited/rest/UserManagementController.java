package com.fantasyunlimited.rest;

import com.fantasyunlimited.data.entity.FantasyUnlimitedUser;
import com.fantasyunlimited.data.entity.PlayerCharacter;
import com.fantasyunlimited.data.enums.UserFoundStatus;
import com.fantasyunlimited.data.service.FantasyUnlimitedUserService;
import com.fantasyunlimited.items.bags.ClassBag;
import com.fantasyunlimited.items.bags.LocationBag;
import com.fantasyunlimited.items.bags.RaceBag;
import com.fantasyunlimited.items.entity.CharacterClass;
import com.fantasyunlimited.items.entity.Location;
import com.fantasyunlimited.items.entity.Race;
import com.fantasyunlimited.rest.dto.*;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/user")
public class UserManagementController {
    private static final Logger log = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private FantasyUnlimitedUserService userService;
    @Autowired
    private LocationBag locationBag;
    @Autowired
    private ClassBag classBag;
    @Autowired
    private RaceBag raceBag;

    @RequestMapping(value = "/characters/create", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> createCharacter(@RequestBody CharacterCreationBody characterCreationBody, HttpServletRequest request) {

        if(userService.characterExistsByName(characterCreationBody.name())) {
            return new ResponseEntity<>("Character exists!", HttpStatus.CONFLICT);
        }

        FantasyUnlimitedUser user = userService.getUser(request.getUserPrincipal().getName());
        try {
            userService.createCharacter(user, characterCreationBody.name(), characterCreationBody.classId(), characterCreationBody.raceId());
        }
        catch(IllegalArgumentException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("OK!", HttpStatus.OK);
    }

    @RequestMapping(value = "/characters", method = RequestMethod.GET)
    public ResponseEntity<List<CharacterListItem>> getPlayerCharacters(HttpServletRequest request) {

        if(request.getUserPrincipal() == null)
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        String userName = request.getUserPrincipal().getName();
        FantasyUnlimitedUser user = userService.getUser(userName);
        log.debug("Found characters for user {}: {}", userName, user.getCharacters());

        HttpStatus responseStatus;
        if(user.getCharacters().isEmpty())
            responseStatus = HttpStatus.NOT_FOUND;
        else
            responseStatus = HttpStatus.OK;

        List<CharacterListItem> items = user.getCharacters().stream()
                .map(character -> {
                    CharacterClass characterClass = classBag.getItem(character.getClassId());
                    ClassItem classItem = new ClassItem(characterClass.getId(), characterClass.getName(), characterClass.getIconName());

                    Race race = raceBag.getItem(character.getRaceId());
                    RaceItem raceItem = new RaceItem(race.getId(), race.getName(), race.getIconName());

                    Location location = locationBag.getItem(character.getLocationId());
                    LocationItem locationItem = new LocationItem(location.getId(), location.getName(), location.getIconName());

                    return new CharacterListItem(
                                    character.getId(),
                                    character.getName(),
                                    classItem,
                                    raceItem,
                                    locationItem,
                                    character.getCurrentLevel());
                }).collect(Collectors.toList());

        return new ResponseEntity<>(items, responseStatus);
    }

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
