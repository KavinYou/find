/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.hod.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.find.core.beanconfiguration.AppConfiguration;
import com.hp.autonomy.frontend.find.core.beanconfiguration.DispatcherServletConfiguration;
import com.hp.autonomy.frontend.find.core.web.ControllerUtils;
import com.hp.autonomy.frontend.find.core.web.FindController;
import com.hp.autonomy.frontend.find.core.web.MvcConstants;
import com.hp.autonomy.frontend.find.core.web.ViewNames;
import com.hp.autonomy.frontend.find.hod.authentication.HodCombinedRequestController;
import com.hp.autonomy.frontend.find.hod.beanconfiguration.HodConfiguration;
import com.hp.autonomy.frontend.find.hod.configuration.HodFindConfig;
import com.hp.autonomy.hod.client.error.HodErrorException;
import com.hp.autonomy.hod.sso.HodAuthenticationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SsoController {

    public static final String SSO_PAGE = "/sso";
    public static final String SSO_AUTHENTICATION_URI = "/authenticate-sso";
    public static final String SSO_LOGOUT_PAGE = "/sso-logout";
    public static final String HOD_SSO_ERROR_PARAM = "error";

    private final HodAuthenticationRequestService hodAuthenticationRequestService;
    private final HodErrorController hodErrorController;
    private final ConfigService<HodFindConfig> configService;
    private final ControllerUtils controllerUtils;
    private final String gitCommit;
    private final String ssoPage;
    private final String logoutEndpoint;

    @Autowired
    public SsoController(
            final HodAuthenticationRequestService hodAuthenticationRequestService,
            final ConfigService<HodFindConfig> configService,
            final ControllerUtils controllerUtils,
            final HodErrorController hodErrorController,
            @Value(AppConfiguration.GIT_COMMIT_PROPERTY) final String gitCommit,
            @Value(HodConfiguration.SSO_PAGE_PROPERTY) final String ssoPage,
            @Value(HodConfiguration.HOD_API_URL_PROPERTY) final String logoutEndpoint
    ) {
        this.hodAuthenticationRequestService = hodAuthenticationRequestService;
        this.configService = configService;
        this.controllerUtils = controllerUtils;
        this.hodErrorController = hodErrorController;
        this.gitCommit = gitCommit;
        this.ssoPage = ssoPage;
        this.logoutEndpoint = logoutEndpoint;
    }

    @RequestMapping(value = SSO_PAGE, method = RequestMethod.GET, params = HOD_SSO_ERROR_PARAM)
    public ModelAndView ssoError(final HttpServletRequest request, final HttpServletResponse response) throws HodErrorException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return hodErrorController.authenticationErrorPage(request, response);
    }

    @RequestMapping(value = SSO_PAGE, method = RequestMethod.GET)
    public ModelAndView sso(final ServletRequest request) throws JsonProcessingException, HodErrorException {
        final Map<String, Object> ssoConfig = new HashMap<>();
        ssoConfig.put(SsoMvcConstants.AUTHENTICATE_PATH.value(), SSO_AUTHENTICATION_URI);
        ssoConfig.put(SsoMvcConstants.COMBINED_REQUEST_API.value(), HodCombinedRequestController.COMBINED_REQUEST);
        ssoConfig.put(SsoMvcConstants.ERROR_PAGE.value(), DispatcherServletConfiguration.CLIENT_AUTHENTICATION_ERROR_PATH);
        ssoConfig.put(SsoMvcConstants.COOKIE_ERROR_PAGE.value(), DispatcherServletConfiguration.COOKIE_AUTHENTICATION_ERROR_PATH);
        ssoConfig.put(SsoMvcConstants.LIST_APPLICATION_REQUEST.value(), hodAuthenticationRequestService.getListApplicationRequest());
        ssoConfig.put(SsoMvcConstants.SSO_PAGE.value(), ssoPage);
        ssoConfig.put(SsoMvcConstants.SSO_ENTRY_PAGE.value(), SSO_PAGE);

        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(MvcConstants.GIT_COMMIT.value(), gitCommit);
        attributes.put(MvcConstants.CONFIG.value(), controllerUtils.convertToJson(ssoConfig));
        attributes.put(ControllerUtils.SPRING_CSRF_ATTRIBUTE, request.getAttribute(ControllerUtils.SPRING_CSRF_ATTRIBUTE));

        return new ModelAndView(ViewNames.SSO.viewName(), attributes);
    }

    @RequestMapping(value = SSO_LOGOUT_PAGE, method = RequestMethod.GET)
    public ModelAndView ssoLogoutPage(final ServletRequest request) throws JsonProcessingException {
        final HodFindConfig hodFindConfig = configService.getConfig();

        final Map<String, Object> ssoConfig = new HashMap<>();
        ssoConfig.put(SsoMvcConstants.LOGOUT_ENDPOINT.value(), logoutEndpoint);
        ssoConfig.put(SsoMvcConstants.LOGOUT_REDIRECT_URL.value(), hodFindConfig.getHsod().getLandingPageUrl());

        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(MvcConstants.GIT_COMMIT.value(), gitCommit);
        attributes.put(MvcConstants.CONFIG.value(), controllerUtils.convertToJson(ssoConfig));
        attributes.put(ControllerUtils.SPRING_CSRF_ATTRIBUTE, request.getAttribute(ControllerUtils.SPRING_CSRF_ATTRIBUTE));
        return new ModelAndView(ViewNames.SSO_LOGOUT.viewName(), attributes);
    }
}
