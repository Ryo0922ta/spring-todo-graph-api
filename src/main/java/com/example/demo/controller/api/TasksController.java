package com.example.demo.controller.api;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.TasksDTO;
import com.example.demo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

	private final TaskService taskService;

	public TasksController(TaskService taskService) {
		this.taskService = taskService;
	}

	//	URL は「リソース（名詞）」を表す
	//	HTTP メソッドは「操作（動詞）」を表す

	@GetMapping()
	public List<TasksDTO> getAllTask() {
		List<TasksDTO> tasks = taskService.findAllTasks();
		return tasks;
	}

	@GetMapping("{taskId}")
	public TasksDTO getTask(@PathVariable Long taskId) {
		TasksDTO taskDto = taskService.findTask(taskId);
		return taskDto;
	}

	//postは新規登録などのみ
	@PostMapping()
	public TasksDTO add(@RequestBody TasksDTO taskDto) {
		TasksDTO newTasksDto = taskService.saveTask(taskDto);
		return newTasksDto;
	}

	//更新はPut
	@PutMapping("{taskId}")
	public TasksDTO update(@PathVariable Long taskId, @RequestBody TasksDTO tasksDTO) {
		tasksDTO.setTaskId(taskId);
		TasksDTO taskDto = taskService.updateTask(tasksDTO);
		return taskDto;
	}

	//削除　　@PathVariale("id")は冗長だから記述いらない
	@DeleteMapping("{taskId}")
	public void delete(@PathVariable Long taskId) {
		Integer deleteCount = taskService.deleteTask(taskId);
		System.out.println("削除タスク件数: " + deleteCount + "件");
		return;
	}

}
