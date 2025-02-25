package com.climbers.hangangactivity.controller.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.climbers.hangangactivity.model.Program;
import com.climbers.hangangactivity.service.ProgramService;

@RestController
@RequestMapping("/api/programs")
public class ProgramController {

    private static final Logger logger = LoggerFactory.getLogger(ProgramController.class);
    
    private final ProgramService programService;

    public ProgramController(ProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    public List<Program> getPrograms() {
        return programService.getPrograms();
    }

    @GetMapping("/{id}")
    public Program getProgramById(@PathVariable int id) {
        return programService.getProgramById(id);
    }

    @PostMapping
    public void createProgram(@RequestBody Program program) {
        programService.createProgram(program);
    }

    @PutMapping("/{id}")
    public void updateProgram(@PathVariable int id, @RequestBody Program program) {
        program.setId(id);
        programService.updateProgram(program);
    }

    @DeleteMapping("/{id}")
    public void deleteProgram(@PathVariable int id) {
        programService.deleteProgram(id);
    }
}
