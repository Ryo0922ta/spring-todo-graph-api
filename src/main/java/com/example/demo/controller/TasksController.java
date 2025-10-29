package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.dto.TasksDTO;
import com.example.demo.model.entity.Tasks;
import com.example.demo.service.TaskService;

@Controller
public class TasksController {

	private final TaskService taskService;

	public TasksController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/tasks")
	public String index(Model model) {
		List<TasksDTO> tasks = taskService.findAllTasks();
		System.out.println("Controller受け取り件数: " + tasks.size());
		model.addAttribute("tasks", tasks);
		return "index";
	}

	//	@ModelAttribute("Task")でHTMLフォームの文字列がJava型に変換される

	@PostMapping("/add")
	public String add(@ModelAttribute("Task") Tasks task) {
		Integer addCount = taskService.saveTask(task);
		System.out.println("追加タスク件数: " + addCount);
		return "redirect:/tasks";
	}

	//更新
	@PostMapping("/update")
	public String update(@ModelAttribute("Task") Tasks task) {
		Integer updateCount = taskService.updateTask(task);
		System.out.println("更新タスク件数: " + updateCount);
		return "redirect:/tasks";
	}

	//削除
	@PostMapping("/delete")
	public String delete(@RequestParam Long taskId) {
		Integer deleteCount = taskService.deleteTask(taskId);
		System.out.println("削除タスク件数: " + deleteCount);
		return "redirect:/tasks";
	}

}
