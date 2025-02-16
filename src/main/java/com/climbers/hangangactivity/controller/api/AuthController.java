package com.climbers.hangangactivity.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.controller.company.CompanyPageController;
import com.climbers.hangangactivity.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyPageController.class);

    @Autowired
    private UserService userService;

 
}