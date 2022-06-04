package com.fantasyunlimited.security;

import com.fantasyunlimited.rest.dto.ActiveUserItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component("authSuccessHandler")
public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);

        final List<ActiveUserItem> activeUsers = new ArrayList<>();
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        allPrincipals.stream()
                .filter(principal -> principal instanceof User)
                .map(principal -> (User) principal)
                .map(user -> new ActiveUserItem(user.getUsername(), user.getAuthorities().toString()))
                .forEach(activeUsers::add);

        template.convertAndSend("/topic/users", activeUsers);
    }
}