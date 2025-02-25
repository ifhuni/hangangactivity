package com.climbers.hangangactivity.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.climbers.hangangactivity.mapper.ProgramMapper;
import com.climbers.hangangactivity.model.Program;

@Service
public class ProgramService {
    private final ProgramMapper programMapper;

    public ProgramService(ProgramMapper programMapper) {
        this.programMapper = programMapper;
    }

    public List<Program> getPrograms() {
        return programMapper.getPrograms();
    }

    public Program getProgramById(int id) {
        return programMapper.getProgramById(id);
    }

    public void createProgram(Program program) {
        programMapper.createProgram(program);
    }

    public void updateProgram(Program program) {
        programMapper.updateProgram(program);
    }

    public void deleteProgram(int id) {
        programMapper.deleteProgram(id);
    }
}