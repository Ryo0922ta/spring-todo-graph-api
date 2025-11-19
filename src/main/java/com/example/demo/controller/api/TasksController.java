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

	private static final String ID = "id";
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

	@GetMapping("{id}")
	public TasksDTO getTask(@PathVariable(ID) Long id) {
		TasksDTO dto = taskService.findTask(id);
		return dto;
	}

	//postは新規登録などのみ
	@PostMapping()
	public void add(@RequestBody TasksDTO taskDto) {
		Integer addCount = taskService.saveTask(taskDto);
		System.out.println("追加タスク" + addCount + "件");
		return;
	}

	//更新はPut
	@PutMapping("{id}")
	public void update(@PathVariable(ID) Long id, @RequestBody TasksDTO tasksDTO) {
		tasksDTO.setTaskId(id);
		Integer updateCount = taskService.updateTask(tasksDTO);
		System.out.println("更新タスク件数: " + updateCount + "件");
		return;
	}

	//削除
	@DeleteMapping("{id}")
	public void delete(@PathVariable(ID) Long id) {
		Integer deleteCount = taskService.deleteTask(id);
		System.out.println("削除タスク件数: " + deleteCount + "件");
		return;
	}

}
