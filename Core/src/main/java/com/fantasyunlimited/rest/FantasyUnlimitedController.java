package com.fantasyunlimited.rest;

import com.fantasyunlimited.items.entity.Weapon;
import com.fantasyunlimited.items.util.ItemUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
@Tag(name = "Foo")
public class FantasyUnlimitedController {
    private static final Logger log = LoggerFactory.getLogger(FantasyUnlimitedController.class);
    private static final String PATH = "/error";

    @Autowired
    private ItemUtils itemUtils;

    @RequestMapping("/game")
    public String game() {
        return "Game!";
    }

    @RequestMapping(value = "/weapon", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Weapon", description = "Weapon oida!")
    public ResponseEntity<Weapon> getWeapon() {
        return new ResponseEntity<>(itemUtils.getWeapon("broken-sword"), HttpStatus.FOUND);
    }
}
