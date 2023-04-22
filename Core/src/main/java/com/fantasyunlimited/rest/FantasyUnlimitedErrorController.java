package com.fantasyunlimited.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FantasyUnlimitedErrorController implements ErrorController {
    private static final Logger log = LoggerFactory.getLogger(FantasyUnlimitedErrorController.class);

    @RequestMapping("/error")
    @ResponseStatus(HttpStatus.OK)
    public String error(HttpServletRequest request) {
        try {
            Object originUrl = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
            Object errorCode = request.getAttribute("jakarta.servlet.error.status_code");
            Object errorMessage = request.getAttribute("jakarta.servlet.error.message");

            log.debug("Error: HTTP {} - {} - {}", errorCode, errorMessage, originUrl);

            if(originUrl == null || originUrl.toString().startsWith("/gs-guide-websocket")){
                return null;
            }
        }
        catch(Exception e) {
            log.error("Error while handling HTTP error.", e);
        }
        return "forward:/index.html";
    }
}
