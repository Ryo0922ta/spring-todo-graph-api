package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.entity.Tasks;

//interface : 接続面,繋ぎめ
public interface TaskRepository {
	//タスクの全件取得	
	public List<Tasks> findAllTasks();

	//任意のタスクを取得
	public Tasks findTasks(Long taskId);

	//タスクの保存
	public Integer saveTask(Tasks task);

	//タスクの更新
	public Integer updateTask(Tasks task);

	//タスクの削除
	public Integer deleteTask(Long taskId);
}
