package com.climbers.hangangactivity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.climbers.hangangactivity.model.Program;

@Mapper
public interface ProgramMapper {
    @Select("SELECT * from program pg left join company c on pg.company_id = c.id ")
    List<Program> getPrograms();

    @Select("SELECT * FROM program WHERE id = #{id}")
    Program getProgramById(int id);

    @Insert("INSERT INTO program (company_id, title, description, price, capacity, start_time, end_time) " +
            "VALUES (#{companyId}, #{title}, #{description}, #{price}, #{capacity}, #{startTime}, #{endTime})")
    void createProgram(Program program);

    @Update("UPDATE program SET company_id = #{companyId}, title = #{title}, description = #{description}, " +
            "price = #{price}, capacity = #{capacity}, start_time = #{startTime}, end_time = #{endTime} WHERE id = #{id}")
    void updateProgram(Program program);

    @Delete("DELETE FROM program WHERE id = #{id}")
    void deleteProgram(int id);
}