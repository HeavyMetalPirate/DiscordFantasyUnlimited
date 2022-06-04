package com.fantasyunlimited.rest;

import com.fantasyunlimited.rest.dto.ActiveUserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class WebSocketController {
    @Autowired
    private SessionRegistry sessionRegistry;

    @MessageMapping("/actives")
    @SendTo("/topic/users")
    public List<ActiveUserItem> getActiveUsers() throws Exception {
        final List<ActiveUserItem> activeUsers = new ArrayList<>();

        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for(final Object principal : allPrincipals) {
            if(principal instanceof User user) {
                // Do something with user
                activeUsers.add(new ActiveUserItem(user.getUsername(), user.getAuthorities().toString()));
            }
        }
        return activeUsers;
    }
}
