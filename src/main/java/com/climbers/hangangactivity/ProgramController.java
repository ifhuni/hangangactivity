package com.climbers.hangangactivity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/program")
public class ProgramController {
    
    @GetMapping("/land")
    public String land() {
        return "program/land";
    }

    @GetMapping("/water")
    public String water() {
        return "program/water";
    }

    @GetMapping("/air")
    public String air() {
        return "program/air";
    }

    @GetMapping("home")
    public String home() {
        return "program/home";
    }
}
