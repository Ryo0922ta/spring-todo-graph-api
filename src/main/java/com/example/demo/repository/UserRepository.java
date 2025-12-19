package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.entity.User;

public interface UserRepository {
	//ユーザーの全件取得	
	public List<User> findAllUsers();

	//任意のユーザーを取得
	public User findUserByEmail(String email);

	//IDから任意のユーザーを取得
	public User findUserById(Integer userId);

	//ユーザーの登録
	public void saveUser(User user);
}
