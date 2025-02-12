package com.climbers.hangangactivity.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPageController {

 private static final Logger logger = LoggerFactory.getLogger(UserPageController.class);

    @GetMapping("/")
    public String showUserIndexPage() {
        logger.debug("showUserIndexPage");
        return "user/index"; 
    }
}