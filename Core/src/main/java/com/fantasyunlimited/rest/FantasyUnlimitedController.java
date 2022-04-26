package com.fantasyunlimited.rest;

import com.fantasyunlimited.items.entity.Weapon;
import com.fantasyunlimited.items.util.ItemUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
@Tag(name = "Foo")
public class FantasyUnlimitedController {

    @Autowired
    private ItemUtils itemUtils;

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @Operation(summary = "Foo", description = "Does Foo!")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("Pong!", HttpStatus.OK);
    }

    @RequestMapping(value = "/weapon", method = RequestMethod.GET, produces = "application/json")
    @Operation(summary = "Weapon", description = "Weapon oida!")
    public ResponseEntity<Weapon> getWeapon() {
        return new ResponseEntity<>(itemUtils.getWeapon("broken-sword"), HttpStatus.FOUND);
    }
}
