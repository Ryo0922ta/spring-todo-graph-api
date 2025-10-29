package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.converter.TaskConverter;
import com.example.demo.model.dto.TasksDTO;
import com.example.demo.model.entity.Tasks;
import com.example.demo.repository.TaskRepository;

@Service
public class TaskService {

	private final TaskRepository taskRepository;
	private final TaskConverter taskConverter;

	public TaskService(TaskRepository taskRepository, TaskConverter taskConverter) {
		this.taskRepository = taskRepository;
		this.taskConverter = taskConverter;
	}

	//タスクの全件取得
	public List<TasksDTO> findAllTasks() {
		List<Tasks> taskList = taskRepository.findAllTasks();
		//		リストの中身を一つずつストリームで流し、変換し、まとめ直す
		List<TasksDTO> taskListDTO = taskList
				.stream()
				.map(task -> taskConverter.toTasksDTO(task))
				.collect(Collectors.toList());
		return taskListDTO;
	}

	//タスクの追加
	public Integer saveTask(Tasks task) {
		Integer saveCount = taskRepository.saveTask(task);
		return saveCount;

	}

	//タスク更新
	public Integer updateTask(Tasks task) {
		Integer updateCount = taskRepository.updateTask(task);
		return updateCount;
	}

	//タスクの削除
	public Integer deleteTask(Long taskId) {
		Integer deleteCount = taskRepository.deleteTask(taskId);
		return deleteCount;
	}

}
