package com.example.demo.converter;

import org.springframework.stereotype.Component;

import com.example.demo.model.dto.TasksDTO;
import com.example.demo.model.entity.Tasks;

@Component
public class TaskConverter {

	public TasksDTO toTasksDTO(Tasks taskEntity) {
		TasksDTO dto = new TasksDTO();

		dto.setTaskId(taskEntity.getTaskId());
		dto.setTaskName(taskEntity.getTaskName());
		dto.setImportance(taskEntity.getImportance());
		dto.setUrgency(taskEntity.getUrgency());
		dto.setUserId(taskEntity.getUserId());
		dto.setStateId(taskEntity.getStateId());
		return dto;
	}

	public Tasks toTaskEntity(TasksDTO taskdto) {
		Tasks entity = new Tasks();

		entity.setTaskId(taskdto.getTaskId());
		entity.setTaskName(taskdto.getTaskName());
		entity.setImportance(taskdto.getImportance());
		entity.setUrgency(taskdto.getUrgency());
		entity.setUserId(taskdto.getUserId());
		entity.setStateId(taskdto.getStateId());
		return entity;
	}

	//	MapStruct使おう。。。

}
