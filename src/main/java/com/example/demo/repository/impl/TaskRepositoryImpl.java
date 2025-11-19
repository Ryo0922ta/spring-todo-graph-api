package com.example.demo.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.entity.Tasks;
import com.example.demo.repository.TaskRepository;

//DI理解のため@RequiredArgsConstructorはつかわない
//interface : 接続面,繋ぎめ であるからこのクラスはこの接続面と繋がっている実装部分という意味
@Repository
public class TaskRepositoryImpl implements TaskRepository {

	private final TaskMapper taskMapper;

	public TaskRepositoryImpl(TaskMapper taskMapper) {
		this.taskMapper = taskMapper;
	}

	@Override
	public List<Tasks> findAllTasks() {
		List<Tasks> list = taskMapper.selectAlltask();
		return list;
	}

	@Override
	public Tasks findTasks(Long taskId) {
		Tasks task = taskMapper.selectTask(taskId);
		return task;
	}

	@Override
	public void saveTask(Tasks task) {
		taskMapper.saveTask(task);
		return;
	}

	@Override
	public Integer updateTask(Tasks task) {
		Integer updateCount = taskMapper.updateTask(task);
		return updateCount;
	}

	@Override
	public Integer deleteTask(Long taskId) {
		Integer deleteCount = taskMapper.deleteTask(taskId);
		return deleteCount;
	}

}
