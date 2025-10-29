package com.example.demo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.demo.model.entity.Tasks;

@Mapper
public interface TaskMapper {

	public List<Tasks> selectAlltask();

	public Integer saveTask(Tasks task);

	public Integer updateTask(Tasks task);

	public Integer deleteTask(Long taskId);

}
