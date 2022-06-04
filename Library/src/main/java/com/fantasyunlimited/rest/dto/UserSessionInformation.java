package com.fantasyunlimited.rest.dto;

import org.springframework.security.web.csrf.CsrfToken;

import java.security.Principal;

public record UserSessionInformation(Principal principal, CsrfToken csrf, String character) {}
